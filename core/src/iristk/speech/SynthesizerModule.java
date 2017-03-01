/*******************************************************************************
 * Copyright (c) 2014 Gabriel Skantze.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Gabriel Skantze - initial API and implementation
 ******************************************************************************/
package iristk.speech;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.sound.sampled.UnsupportedAudioFileException;

import org.slf4j.Logger;
import iristk.audio.AudioTarget;
import iristk.audio.AudioUtil;
import iristk.audio.Sound;
import iristk.audio.SoundPlayer;
import iristk.audio.SoundPlayer.CallbackDelegate;
import iristk.project.Project;
import iristk.speech.Voice.Gender;
import iristk.system.InitializationException;
import iristk.system.Event;
import iristk.system.IrisModule;
import iristk.system.IrisUtils;
import iristk.util.EditDistance;
import iristk.util.Language;
import iristk.util.Pair;
import iristk.util.Record;
import iristk.util.Utils;

public class SynthesizerModule extends IrisModule {
	
	private static Logger logger = IrisUtils.getLogger(SynthesizerModule.class);

	//private Synthesizer synthesizer;
	private SoundPlayer player;
	private SpeechThread speechThread = null;
	ArrayList<String> monitorStates = new ArrayList<String>();
	private int endPosition;
	private boolean doLipsync = false;
	private String agentName = "system";
	//private String preferredAudioDevice = null;
	private SpeechQueue speechQueue = new SpeechQueue();

	//private HashMap<Integer,SoundPlayer> soundPlayers = new HashMap<>();

	private boolean speaking = false;
	
	private VoiceList voices = new VoiceList();

	private List<Synthesizer> synthesizers = new ArrayList<>();
	private Map<Voice,SynthesizerEngine> engines = new HashMap<>();
	private Voice currentVoice;
	private SynthesizerEngine currentEngine;

	public SynthesizerModule() {
		this.player = new SoundPlayer(AudioUtil.getAudioFormat(16000, 1));
	}
	
	public SynthesizerModule(AudioTarget audioTarget) {
		this.player = new SoundPlayer(audioTarget);
	}

	public SynthesizerModule(Synthesizer synth) throws InitializationException {
		this.player = new SoundPlayer(AudioUtil.getAudioFormat(16000, 1));
		addSynthesizer(synth);
	}

	public void addSynthesizer(Synthesizer synthesizer) throws InitializationException {
		synthesizers.add(synthesizer);
		this.voices.addAll(synthesizer.getVoices());
	}

	/*
	public void setSynthesizer(Synthesizer synthesizer) throws InitializationException {
		stopSpeaking();
		if (!synthesizers.contains(synthesizer))
			addSynthesizer(synthesizer);
		this.synthesizer = synthesizer;
		//setSoundplayer();
	}
	*/

	/*
	private void setSoundplayer() throws InitializationException {
		if (synthesizer == null)
			throw new InitializationException("Synthesizer not set, cannot set sound player");
		if (synthesizer.getVoice() == null)
			throw new InitializationException("Voice not set, cannot set sound player");
		setSoundplayer(synthesizer.getAudioFormat());
	}

	private void setSoundplayer(AudioFormat audioFormat) throws InitializationException {
		int sampleRate = (int) audioFormat.getSampleRate();
		if (!soundPlayers.containsKey(sampleRate)) {
			this.player = new SoundPlayer(new Speaker(preferredAudioDevice, audioFormat));
			soundPlayers.put(sampleRate, player);
		} else {
			this.player = soundPlayers.get(sampleRate);
		}
	}
	*/

	//public void setPreferredAudioDevice(String deviceName) {
	//	this.preferredAudioDevice = deviceName;
	//}

	public void doLipsync(boolean flag) {
		this.doLipsync = flag;
	}

	@Override
	public void init() throws InitializationException {
		subscribe("action.speech** monitor.lipsync** action.voice");
		speechThread = new SpeechThread();
	}

	//public AudioFormat getAudioFormat() {
	//	return getSynthesizer().getAudioFormat();
	//}

	//public Synthesizer getSynthesizer() {
	//	return synthesizer;
	//}

	public AudioTarget getAudioTarget() {
		return player.getAudioTarget();
	}

	/*
	public void say(String text) {
		sayAsync(text);
		try {
			speechThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void sayAsync(String text) {
		Event event = new Event("action.speech");
		event.put("text", text);
		if (speechThread != null && speechThread.isRunning()) {
			speechThread.stopRunning();
		}
		speechThread = new SpeechThread(event);
	}
	 */

	@Override
	public void onEvent(Event event) {
		if (event.has("agent") && !event.getString("agent").equals(agentName)) 
			return;
		if (event.triggers("action.speech")) {
			if (event.has("agent") && !event.getString("agent").equals(agentName))
				return;
			boolean ifsilent = event.getBoolean("ifsilent", false);
			boolean async = event.getBoolean("async", false);
			if (ifsilent && speaking) {
				if (!async)
					monitorDone();
				return;
			}
			boolean monitorWords = event.getBoolean("monitorWords", false);
			String text = event.getString("text");
			String audio = event.getString("audio");
			Transcription phones = (Transcription) event.get("phones");
			String display = event.getString("display");
			boolean abort = event.getBoolean("abort", false);
			if (abort) 
				speechQueue.clear();
			speechQueue.append(event.getId(), text, audio, phones, display, monitorWords);
			if (abort) 
				abortSpeechAction();

			//if (speechThread != null && speechThread.isRunning()) {
			//	speechThread.stopRunning();
			//}

			/*
			if (event.getString("text", "").replaceAll("\\s*<.*?>\\s*", "").trim().length() == 0) {
				// The event doesn't contain anything to synthesize, so just skip it
				Event offset = new Event("monitor.speech.end");
				offset.put("action", event.getId());
				if (event.has("agent"))
					offset.put("agent", event.getString("agent"));
				send(offset);
			} else {
				speechThread = new SpeechThread(event);
			}
			 */
		} else if (event.triggers("action.speech.stop")) {
			if (!event.has("agent") || event.getString("agent").equals(agentName)) {
				stopSpeaking();
			}
		} else if (event.triggers("monitor.lipsync.start")) {
			speechThread.lipsReady(event);
		} else if (event.triggers("action.voice")) {
			if (!event.has("agent") || event.getString("agent").equals(agentName)) { 
				try {
					if (event.has("name")) {
						setVoice(event.getString("name"));
					} else if (event.has("gender")) {
						setVoice(Gender.fromString(event.getString("gender")));
					}
				} catch (VoiceNotFoundException e) {
					logger.error(e.getMessage());
				} catch (InitializationException e) {
					logger.error("Problem initializing voice engine: " + e.getMessage());
				}
			}
		}
	}

	private void stopSpeaking() {
		speechQueue.clear();
		abortSpeechAction();
	}
	
	private void abortSpeechAction() {
		if (speechThread != null) {
			speechThread.abortSpeechAction();
			if (doLipsync && speechThread.sa != null) {
				Event lipsync = new Event("action.lipsync.stop");
				lipsync.put("action", speechThread.sa.action);
				send(lipsync);
			}
		}
	}

	protected void addMonitorState(String state) {
		monitorStates.add(state);
		monitorState(monitorStates.toArray(new String[0]));
	}

	protected void removeMonitorState(String state) {
		monitorStates.remove(state);
		monitorState(monitorStates.toArray(new String[0]));
	}

	private class SpeechThread extends Thread {

		//private final Event speechEvent;
		//private boolean running;
		private boolean lipsReady = false;
		private boolean playing = false;
		//private Object lock = new Object();
		public SpeechAction sa = null;
		private ArrayBlockingQueue<Event> lipsEvents = new ArrayBlockingQueue<>(1000);

		public SpeechThread() {
			super("SpeechThread");
			start();
		}

		public void abortSpeechAction() {
			if (sa != null) {
				sa.abort = true;
				if (playing)
					player.stop();
			}
		}

		@Override
		public void run() {
			try {
				while (true) {
					sa = null;
					sa = speechQueue.take();
					
					if (currentVoice == null) {
						logger.info("No voice selected, using " + voices.getFirst().getName());
						setVoice(voices.getFirst());
					}
					
					if (sa.text == null ){ //Changed.
						logger.warn("AUDIO , sa.text is null Line 296 in synthesizermodule");
						sa.text = "";
					} else if(sa.text.equals("")){
						logger.warn("AUDIO , sa.text is empty Line 299 in synthesizermodule");
						sa.text="";
					}
					speaking = true;

					final int startMsec = 0; //speechEvent.getInteger("start", 0);

					Transcription trans = null;
					File wavFile = null;

					if (sa.audio != null) {
						wavFile = getAudioFile(sa.audio, currentVoice);
						if (wavFile != null) {
							if (sa.phones != null) {
								trans = sa.phones;
							} else {
								trans = getTranscription(wavFile, currentEngine, sa.text);
								if (trans == null)
									trans = new Transcription();
							}
						} else {
							logger.error("Audio file not found: " + sa.audio);
						}
					}

					if (wavFile == null) {
						Pair<Transcription,File> pair = synthesizeToCache(sa.text, currentEngine); 
						trans = pair.getFirst();
						wavFile = pair.getSecond();
					}
					
					String[] textWords = getTextWords(sa.text);
					String[] transWords = trans.getWords().toArray(new String[0]);
					EditDistance trans2text = new EditDistance(transWords, textWords) {
						@Override
						protected float insertionCost(String w) {
							if (w.startsWith("/")) return 0; else return 1f;
						}
					};

					if (doLipsync && trans != null) {

						Event lipsync = new Event("action.lipsync");
						lipsync.put("action", sa.action);
						if (startMsec > 0)
							lipsync.put("start", startMsec);
						lipsync.put("phones", trans);
						if (agentName != null)
							lipsync.put("agent", agentName);
						send(lipsync);
						AWAIT_LIPSYNC:
							while (true) {
								Event event = lipsEvents.poll(5000, TimeUnit.MILLISECONDS);
								if (event == null) {
									System.err.println("WARNING: No response for lipsync (no agent running?), turning lipsync off");
									doLipsync = false;
									break AWAIT_LIPSYNC;
								} else if (event.getString("action").equals(sa.action)) {
									break AWAIT_LIPSYNC;
								}
							}
					}

					if (sa.abort) continue;

					try { // this try catch causes ioexception
						Sound sound = new Sound(wavFile); // Most likely cause of crash.

						if (sound.getAudioFormat().getSampleRate() != 16000f) {
							// TODO: should convert to mono as well
							sound = sound.resample(16000);
							sound.save(wavFile);
						}
						
						long startTime = System.currentTimeMillis();
						//if (player.getAudioFormat().getSampleRate() != sound.getAudioFormat().getSampleRate()) {
						//	try {
						//		setSoundplayer(sound.getAudioFormat());
						//	} catch (InitializationException e) {
						//		logger.error("Problem setting sound player", e);
						//	}
						//}
						playing = true;
						player.playAsync(sound, startMsec, new CallbackDelegate() {
							@Override
							public void callback(int pos) {
								endPosition = (int) (AudioUtil.byteLengthToSeconds(player.getAudioFormat(), pos) * 1000f);
								playing = false;
							}
						});

						final String stateLabel = "\"" + sa.text.replaceAll("<.*?>", "") + "\"";
						addMonitorState(stateLabel);

						Event onset = new Event("monitor.speech.start");
						onset.put("action", sa.action);
						onset.put("text", sa.text);
						if (startMsec > 0)
							onset.put("start", startMsec);
						onset.put("length", (int)(sound.getSecondsLength() * 1000f));
						//if (prominence != null)
						//	onset.put("prominence", prominence);
						if (agentName != null)
							onset.put("agent", agentName);
						onset.putIfNotNull("display", sa.display);
						onset.putIfNotNull("audio", sa.audio);
						send(onset);

						int transpos = 0;
						int wordpos = 0;
						int markpos = findMark(0, textWords);
						if (trans.phones.size() > 0) {
							Phone phone = trans.phones.get(0);
							while (playing) {
								float t = (System.currentTimeMillis() - startTime) / 1000f;
								if (phone != null && t >= phone.start) {
									if (phone.prominent) {
										Event prom = new Event("monitor.speech.prominence");
										prom.put("action", sa.action);
										send(prom);
										//System.out.println("prominent");
									}
									if (phone.word != null) {
										if (markpos == 0 || (markpos > 0 && trans2text.mapP2toP1(markpos) < wordpos)) {
											Event prom = new Event("monitor.speech.mark");
											prom.put("name", textWords[markpos].replace("/", ""));
											prom.put("action", sa.action);
											//System.out.println(prom.getString("name"));
											send(prom);
											markpos = findMark(markpos+1, textWords);
										}
										//sa.monitorWords = true;
										if (sa.monitorWords) {
											Event prom = new Event("monitor.speech.word");
											prom.put("action", sa.action);
											prom.put("word", phone.word);
											prom.put("pos", wordpos);
											send(prom);
											//System.out.println(phone.word);
										}
										wordpos++;
									}
									transpos++;
									if (transpos < trans.phones.size())
										phone = trans.phones.get(transpos);
									else
										phone = null;
								}
								Thread.sleep(10);
							}
						} else {
							while (playing) {
								Thread.sleep(10);
							}
						}
						//System.out.println("Done");
						
						if (markpos >= 0) {
							Event prom = new Event("monitor.speech.mark");
							prom.put("name", textWords[markpos].replace("/", ""));
							//System.out.println(prom.getString("name"));
							send(prom);
						}

						removeMonitorState(stateLabel);
						Event offset = new Event("monitor.speech.end");
						offset.put("action", sa.action);
						if (trans != null && endPosition < trans.length() - 100) {
							offset.put("stopped", endPosition);
						}
						if (agentName != null)
							offset.put("agent", agentName);
						send(offset);

						if (speechQueue.size() == 0) {
							speaking = false;
							monitorDone();
						}

					} catch (UnsupportedAudioFileException e) {
						logger.error("Unsupported audio file: " + wavFile.getAbsolutePath(), e);
					} catch (IOException e) {
						logger.error("Problem opening file: " + wavFile.getAbsolutePath(), e);
					}

				}

			} catch (InterruptedException e1) {
			} catch (VoiceNotFoundException e) {
				logger.error("No voice found");
			} catch (InitializationException e) {
				logger.error("Error initializing synthesizer engine", e);
			}
		}

		public void lipsReady(Event event) {
			//System.out.println("Lips ready! " + event.getString("action").equals(speechEvent.getId()));
			lipsEvents.add(event);
		}

		/*
		public void stopRunning() {
			player.stop();
			running = false;
			playing = false;
			synchronized (SpeechThread.this) {
				SpeechThread.this.notify();
			}
			try {
				join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		public boolean isRunning() {
			return running;
		}
		 */

	}

	private static String cacheId(String text) {
		String id;
		try {
			id = URLEncoder.encode(text, "UTF-8");
			if (id.length() > 100) {
				id = id.substring(0, 100) + text.hashCode();
			}
		} catch (UnsupportedEncodingException e) {
			id = "" + text.hashCode();
		}
		return id;
	}

	public static int findMark(int startPos, String[] words) {
		for (int i = startPos; i < words.length; i++) {
			if (words[i].startsWith("/"))
				return i;
		}
		return -1;
	}

	public void monitorDone() {
		Event done = new Event("monitor.speech.done");
		if (agentName != null)
			done.put("agent", agentName);
		send(done);
	}
	
	public static void main(String[] args) throws URISyntaxException {
		URI uri = new URI("iristk://sing/songs/hej");
		System.out.println(uri.getHost());;
		System.out.println(uri.getPath());;
		System.out.println("c:\\".matches("\\w:.*"));;
	}
	
	public static File getAudioFile(String audio, Voice voice) {
		if (!audio.toLowerCase().endsWith(".wav"))
			audio = audio + ".wav";
		if (audio.matches("https?:.*")) {
			FileOutputStream fos = null;
			try {
				URL url = new URL(audio);
				File cacheDir = IrisUtils.getTempDir("Synthesizer/http");
				cacheDir.mkdirs();
				File cacheFile = new File(cacheDir, audio.hashCode() + ".wav");
				if (cacheFile.exists())
					return cacheFile;
				fos = new FileOutputStream(cacheFile);
				Utils.copyStream(url.openStream(), fos);
				return cacheFile;
			} catch (MalformedURLException e) {
				logger.error("Bad URL: " + audio);
			} catch (IOException e) {
				logger.error("Cannot read from URL: " + audio);
			} finally {
				if (fos != null) {
					try {
						fos.close();
					} catch (IOException e) {
					}
				}
			}
			return null;
		} 
		if (audio.matches("\\w:.*")) {
			// A local file with absolute path
			if (new File(audio).exists())
				return new File(audio);
			else
				return null;
		}
		File file = Project.parseURI(audio);
		if (file != null) {
			// iristk://...
			if (file.exists())
				return file;
			else
				return null;
		} 
		File prerecFile = new File(voice.getPrerecPath(), audio);
		// A file in the prerec folder
		if (prerecFile.exists())
			return prerecFile;
		return null;
	}
	
	private static Transcription getTranscription(File audioFile, SynthesizerEngine engine, String text) {
		Transcription trans = null;
		try {
			File prepho = new File(audioFile.getAbsolutePath().replaceFirst("\\..*", "") + ".pho");
			if (prepho.exists()) {
				trans = (Transcription) Record.fromJSON(Utils.readTextFile(prepho));
			} else {
				File temppho = new File(engine.getVoice().getCachePath(), prepho.getName());
				if (temppho.exists()) {
					trans = (Transcription) Record.fromJSON(Utils.readTextFile(temppho));
				} else {
					trans = engine.transcribe(text);
					Utils.writeTextFile(temppho, trans.toJSON().toString());
				}
			}
		} catch (FileNotFoundException e) {
			logger.error("Could not find audio file", e);
		} catch (IOException e) {
			logger.error("Could not open file", e);
		}
		return trans;
	}
	
	private static String[] getTextWords(String str) {
		return str.replaceAll(" *<mark name=\"(.*?)\"/> *", " /$1/ ").
				replaceAll("<.*?>", "").
				replaceAll("[\\.,\\?\\!]", " ").
				replaceAll("\\s+", " ").
				trim().split(" ");
	}

	//public static void main(String[] args) {
	//	String[] words = getWords("hej <mark id=\"point\"/>, what's <break/>  <usel ke=\"ss\">is</usel> your name");
	//	System.out.println(Arrays.asList(words));
	//}
	
	public static Pair<Transcription,File> synthesizeToCache(String synthText, SynthesizerEngine engine) {
		Transcription trans = null;
		String cacheId = cacheId(synthText);
		if (synthText==null || synthText.equals("")) {
			System.err.println("SynthTextPassed to SynthesizeToCache was bad. So we changed it to ERROR NO SYNTH.");
			cacheId = cacheId("ERROR NO SYNTH");
		}
		File cachePath = engine.getVoice().getCachePath();
		File phoFile = new File(cachePath, cacheId + ".pho");
		File wavFile = new File(cachePath, cacheId + ".wav");
		
		if (!wavFile.exists() || !phoFile.exists()) {
			// Synthesize
			trans = engine.synthesize(removeMarks(synthText), wavFile);
			try {
				Sound sound = new Sound(wavFile);
				byte[] bytes = sound.getBytes();
				double[] samples = new double[bytes.length / 2];
				AudioUtil.bytesToDoubles(sound.getAudioFormat(), bytes, 0, bytes.length, samples, 0);
				double max = Double.MIN_VALUE;
				Phone maxphone = null;
				for (Phone phone : trans.phones) {
					if (phone.name.matches(".*[AOUEIY].*")) {
						double power = AudioUtil.power(samples, 
								AudioUtil.secondLengthToSamples(sound.getAudioFormat(), phone.start), 
								AudioUtil.secondLengthToSamples(sound.getAudioFormat(), phone.end - phone.start));
						if (power > max) {
							max = power;
							maxphone = phone;
						}
					}
				}
				if (maxphone != null)
					maxphone.prominent = true;
			} catch (UnsupportedAudioFileException e1) {
				logger.error("Unsupported audio file", e1);
			} catch (IOException e1) {
				System.err.println("SynthModule wavFile is: "+ cachePath.toString() + " and "+ cacheId + " as both a .pho and .wav file.");
				logger.error("Problem opening file", e1);
			}
			try {
				Utils.writeTextFile(phoFile, trans.toJSON().toString());
			} catch (FileNotFoundException e) {
				System.err.println("SynthModule wavFile is: "+ cachePath.toString() + " and "+ cacheId + " as both a .pho and .wav file.");
				logger.error("File not found", e);
			} catch (IOException e) {
				logger.error("Problem opening file", e);
			}
		} else {
			try {
				trans = (Transcription) Record.fromJSON(Utils.readTextFile(phoFile));
			} catch (FileNotFoundException e) {
				System.err.println("SynthModule phoFile, readingfromTextFile is: "+ cachePath.toString() + " and "+ cacheId + " as both a .pho and .wav file.");
				logger.error("File not found", e);
			} catch (IOException e) {
				logger.error("Problem opening file", e);
			}
		}
		return new Pair<Transcription,File>(trans, wavFile);
	}

	private static String removeMarks(String synthText) {
		return synthText.replaceAll("<mark.*?>","");
	}

	public void setVoice(Language language) throws VoiceNotFoundException, InitializationException {
		setVoice(voices.getByLanguage(language).getFirst());
	}

	public void setVoice(Language language, Gender gender) throws VoiceNotFoundException, InitializationException {
		setVoice(voices.getByLanguage(language).getByGender(gender).getFirst());
	}
	
	public void setVoice(String name) throws VoiceNotFoundException, InitializationException {
		setVoice(voices.getByName(name).getFirst());
	}
	
	public void setVoice(Gender gender) throws VoiceNotFoundException, InitializationException {
		if (currentVoice != null) {
			setVoice(voices.getByGender(gender).getFirst());
		} else {
			setVoice(voices.getByLanguage(currentVoice.getLanguage()).getByGender(gender).getFirst());
		}
	}
	
	public void setVoice(Voice voice) throws VoiceNotFoundException, InitializationException {
		for (Synthesizer synth : synthesizers) {
			for (Voice v : synth.getVoices()) {
				if (voice == v) {
					if (engines.containsKey(voice)) {
						currentEngine = engines.get(voice);
					} else {
						currentEngine = synth.getEngine(voice);
						engines.put(voice, currentEngine);
					}
					this.currentVoice = voice;
					return;
				}
			}
		}
		throw new VoiceNotFoundException(voice);
	}

	public VoiceList getVoices() {
		return voices;
	}

	public Voice getCurrentVoice() {
		return currentVoice;
	}
	
	public SynthesizerEngine getCurrentEngine() {
		return currentEngine;
	}
	
	public void setAgentName(String name) {
		this.agentName = name;
	}

	@Override
	public String getUniqueName() {
		return "synthesizer-" + agentName;
	}

}
