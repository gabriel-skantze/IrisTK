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
package iristk.speech.windows;

import iristk.net.speech.*;
import iristk.audio.AudioGate;
import iristk.audio.AudioListener;
import iristk.audio.AudioPort;
import iristk.cfg.Parser;
import iristk.cfg.SRGSGrammar;
import iristk.speech.EnergyVAD;
import iristk.speech.GrammarRecognizer;
import iristk.speech.EnergyVADContainer;
import iristk.speech.OpenVocabularyContext;
import iristk.speech.OpenVocabularyRecognizer;
import iristk.speech.RecHyp;
import iristk.speech.RecResult;
import iristk.speech.Recognizer;
import iristk.speech.RecognizerException;
import iristk.speech.RecognizerFactory;
import iristk.speech.RecognizerListener;
import iristk.speech.RecognizerListeners;
import iristk.system.InitializationException;
import iristk.system.IrisUtils;
import iristk.util.BlockingByteQueue;
import iristk.util.Language;
import iristk.util.Record;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sound.sampled.AudioFormat;

import org.slf4j.Logger;

public class WindowsRecognizer implements Recognizer, GrammarRecognizer, OpenVocabularyRecognizer, IResultListener, AudioListener, EnergyVADContainer {

	private static Logger logger = IrisUtils.getLogger(WindowsRecognizer.class);
	
	private int adaptation = 0;
	private long startListen;
	private Parser parser;
	private RecognizerListeners listeners = new RecognizerListeners();
	private boolean recognizerRunning = false;
	private boolean inSpeech;
	private boolean partialResults = false;
	private boolean makeNbest = false;
	private AudioPort audioPort;
	//private HashSet<String> activatedGrammars = new HashSet<>();
	private boolean inputToAudioStream = false;
	
	private Map<String,IRecognizer> recognizers = new HashMap<>();
	private Map<String,SpeechAudioStream> streams = new HashMap<>();
	private IRecognizer activeRecognizer = null;
	private SpeechAudioStream activeStream = null;
	private Map<String,Language> contextLanguage = new HashMap<>();
	private int noSpeechTimeout = 5000;
	private int endSilTimeout = 500;
	private int maxSpeechTimeout = 10000;
	private int nbestLength;

	static {
		WindowsSpeech.init();
	}

	public WindowsRecognizer(AudioPort audioPort) throws InitializationException {
		this.audioPort = new AudioGate(audioPort);
		this.audioPort.addAudioListener(this);
		parser = new Parser();
		//System.out.println(hashCode());
	}
	
	/*
	private void setup(Language lang, AudioPort audioPort) throws InitializationException {
		//String supportedLanguages = DesktopRecognizer.getLanguages();
		//if (!supportedLanguages.contains(lang.getCode())) {
		//	throw new InitializationException("Language " + lang  + " not support by WindowsRecognizer. Supported languages: " + supportedLanguages);
		//}
		DesktopRecognizer rec = new DesktopRecognizer(lang.getCode());
		if (rec.success()) {
			setup(rec, audioPort);
		} else {
			throw new InitializationException("Failed to initialize WindowsRecognizer");
		}
	}
	
	private void setup(IRecognizer recognizer, AudioPort audioPort) {
		this.audioPort = audioPort;
		//System.out.println(DesktopRecognizer.getLanguages());
		recognizer.setRecognizerSetting("CFGConfidenceRejectionThreshold", 5);
		recognizer.setRecognizerSetting("AdaptationOn", adaptation);
		recognizer.setRecognizerSetting("PersistedBackgroundAdaptation", adaptation);
		recognizer.registerListener(this);
		parser = new Parser();
		speechRecStream = recognizer.setupAudioStream((int) audioPort.getAudioFormat().getSampleRate(), 32000);
		audioPort.addAudioListener(this);
	}
	*/
	
	protected IRecognizer createRecognizer(Language language) throws RecognizerException {
		if (!DesktopRecognizer.getLanguages().contains(language.getCode())) {
			String supported = DesktopRecognizer.getLanguages().trim();
			if (supported.length() == 0)
				throw new RecognizerException("WindowsRecognizer not supported on this system");
			else
				throw new RecognizerException("Language " + language  + " not supported by WindowsRecognizer. Supported languages: " + supported);
		}
		return new DesktopRecognizer(language.getCode());
	}
	
	protected IRecognizer getRecognizer(Language language) throws RecognizerException {
		if (!recognizers.containsKey(language.getCode())) {
			IRecognizer recognizer = createRecognizer(language);
			recognizer.setRecognizerSetting("CFGConfidenceRejectionThreshold", 5);
			recognizer.setRecognizerSetting("AdaptationOn", adaptation);
			recognizer.setRecognizerSetting("PersistedBackgroundAdaptation", adaptation);
			recognizer.registerListener(this);
			streams.put(language.getCode(), recognizer.setupAudioStream((int) audioPort.getAudioFormat().getSampleRate(), 32000));
			recognizers.put(language.getCode(), recognizer);
			logger.info("Initialized with language " + language.getCode());
		} 
		return recognizers.get(language.getCode());
	}
	
	private void activateRecognizer(Language language) {
		activeRecognizer = recognizers.get(language.getCode());
		activeStream = streams.get(language.getCode());
	}
	
	@Override
	public EnergyVAD getEnergyVAD() {
		if (audioPort instanceof AudioGate) {
			return ((AudioGate)audioPort).getVAD();
		}
		return null;
	}
	
	@Override
	public AudioPort getAudioPort() {
		return audioPort;
	}

	@Override
	public void loadOpenVocabulary(String contextName, OpenVocabularyContext context) throws RecognizerException {
		getRecognizer(context.language).loadDictationGrammar(contextName);
		contextLanguage.put(contextName, context.language);
	}

	@Override
	public void unloadOpenVocabulary(String name) throws RecognizerException {
		// TODO 
	}
	
	@Override
	public void loadGrammar(String name, Language language, URI uri) throws RecognizerException {
		getRecognizer(language).loadGrammarFromPath(name, new File(uri).getAbsolutePath());
		parser.loadGrammar(name, new SRGSGrammar(uri));
		contextLanguage.put(name, language);
	}

	@Override
	public void loadGrammar(String name, Language language, String grammarString) throws RecognizerException {
		//System.out.println("Load " + name);
		grammarString = grammarString.replaceFirst("encoding=\".*?\"", "");
		getRecognizer(language).loadGrammarFromString(name, grammarString);
		contextLanguage.put(name, language);
		try {
			parser.loadGrammar(name, new SRGSGrammar(grammarString));
		} catch (Exception e) {
			throw new RecognizerException(e.getMessage());
		}
	}
	
	@Override
	public void unloadGrammar(String name) throws RecognizerException {
		//TODO
	}
	
	@Override
	public void activateContext(String name, float weight) throws RecognizerException {
		if (contextLanguage.containsKey(name)) {
			//System.out.println("Activate");
			activateRecognizer(contextLanguage.get(name));
			activeRecognizer.activateGrammar(name);
			parser.activateGrammar(name);
		}
	}

	@Override
	public void deactivateContext(String name) throws RecognizerException {
		if (contextLanguage.containsKey(name)) {
			getRecognizer(contextLanguage.get(name)).deactivateGrammar(name);
			parser.deactivateGrammar(name);
		}
		
	}

	@Override
	public void setNoSpeechTimeout(int msec) throws RecognizerException {
		this.noSpeechTimeout = msec;
	}

	@Override
	public void setEndSilTimeout(int msec) throws RecognizerException {
		this.endSilTimeout = msec;
	}

	@Override
	public void setMaxSpeechTimeout(int msec) throws RecognizerException {
		this.maxSpeechTimeout = msec;
	}
	
	static int id = 0;

	@Override
	public void startListen() throws RecognizerException {
		//System.out.println("Starting to recognize: " + this.hashCode());
		if (activeRecognizer == null)
			throw new RecognizerException("No recognizer activated");
		if (!inputToAudioStream)
			activeRecognizer.setInputToAudioStream();
		activeRecognizer.setNoSpeechTimeout(noSpeechTimeout);
		activeRecognizer.setEndSilTimeout(endSilTimeout);
		activeRecognizer.setMaxSpeechTimeout(maxSpeechTimeout);
		activeRecognizer.setMaxAlternates(nbestLength);
		inputToAudioStream = true;
		if (audioPort instanceof AudioGate)
			((AudioGate)audioPort).closeGate();
		startListen = System.currentTimeMillis();
		inSpeech = false;
		recognizerRunning = true;
		gateAudio.reset();
		listeners.initRecognition(getAudioFormat());
		activeRecognizer.recognizeAsync();
	}

	@Override
	public boolean stopListen() throws RecognizerException {
		if (recognizerRunning) {
			//System.out.println("Recognition stopped: " + activeRecognizer.hashCode());
			synchronized (this) {
				activeRecognizer.recognizeCancel();
			}
		} else {
			return false;
		}
		while (recognizerRunning) {
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	private void fillResult(Result result, RecResult recresult) {
		recresult.length = ((float) (result.getLength() / 1000));
		recresult.grammar = result.getGrammar();
		//System.out.println("Grammar: " + recresult.grammar);
		List<RecHyp> nbest = new ArrayList<>();
		for (int i = 0; i < result.size(); i++) {
			Hypothesis winhyp = result.getHypothesis(i);
			RecHyp hyp = new RecHyp();
			hyp.conf = winhyp.getConfidence();
			hyp.text = "";
			for (int wi = 0; wi < winhyp.size(); wi++) {
				iristk.net.speech.Word w = winhyp.getWord(wi);

				hyp.text += " " + w.getText();
			}
			hyp.text = hyp.text.trim();
			/*
			recresult.setSem(parseSemantics(result.getHypothesis(0).getSemantics()));
			*/
			nbest.add(hyp);
		}

		if (nbest.size() > 0) {
			recresult.conf = nbest.get(0).conf;
			recresult.text = nbest.get(0).text;
			if (makeNbest) {
				recresult.nbest = nbest;
			}
		}

		parser.recognitionResult(recresult);

	}
	
	@Override
	public synchronized void recognizeCompleted(Result result) {
		try {
			if (recognizerRunning) {
				if (inSpeech) {
					listeners.endOfSpeech((System.currentTimeMillis() - startListen) / 1000f);
				}
				if (result.isTimeout()) {
					listeners.recognitionResult(new RecResult(RecResult.SILENCE));
				} else if (result.isCancelled()) {
					//listeners.recognitionResult(new RecResult(RecResult.CANCELED));
				} else {
					if (result.size() > 0) {
						RecResult recresult = new RecResult(RecResult.FINAL);
						fillResult(result, recresult);
						listeners.recognitionResult(recresult);
					} else {
						RecResult recresult = new RecResult(RecResult.FINAL, RecResult.NOMATCH);
						recresult.length = 3.0f;
						listeners.recognitionResult(recresult);
					}
				}
				//System.out.println("Recognition completed: " + activeRecognizer.hashCode());
				recognizerRunning = false;
			}
		} catch (Exception e) {
			logger.error("Problem finalizing recognition", e);
		}
	}

	private Record parseSemantics(SemanticStruct struct) {
		if (struct == null)
			return null;
		Record result = new Record();
		for (int i = 0; i < struct.getKeysCount(); i++) {
			SemanticStruct child = struct.getValue(i);
			if (child.getValue() != null) {
				result.put(struct.getKey(i), child.getValue().toString());
			} else {
				result.put(struct.getKey(i), parseSemantics(child));
			}
		}
		return result;
	}

	@Override
	public void recognizeHypothesis(Result result) {
		try {
			if (partialResults && result.size() > 0) {
				String text = result.getHypothesis(0).getText();
				RecResult recresult = new RecResult(RecResult.PARTIAL, text);
				recresult.conf = (result.getHypothesis(0).getConfidence());
				recresult.length = ((float) (result.getLength() / 1000));
				parser.recognitionResult(recresult);
				listeners.recognitionResult(recresult);
			}
		} catch (Exception e) {
			logger.error("Problem processing hypothesis", e);
		}
	}

	@Override
	public void speechDetected(int audioLevel) {
		try {
			if (recognizerRunning) {
				inSpeech = true;
				listeners.startOfSpeech((System.currentTimeMillis() - startListen) / 1000f);
			}
		} catch (Exception e) {
			logger.error("Problem in speech detection", e);
		}
	}

	@Override
	public RecResult recognizeFile(File file) throws RecognizerException {
		inputToAudioStream  = false;
		activeRecognizer.setInputToWaveFile(file.getAbsolutePath());
		Result result = activeRecognizer.recognize();
		RecResult recresult;
		if (result.size() == 0) {
			recresult = new RecResult(RecResult.FAILED);
		} else {
			recresult = new RecResult(RecResult.FINAL);
			fillResult(result, recresult);
		}
		return recresult;
	}

	@Override
	public void addRecognizerListener(RecognizerListener listener, int priority) {
		listeners.add(listener, priority);
	}

	@Override
	public void setPartialResults(boolean cond) {
		partialResults = cond;
	}

	@Override
	public AudioFormat getAudioFormat() {
		return audioPort.getAudioFormat();
	}

	@Override
	public void setNbestLength(int length) {
		makeNbest  = (length > 1);
		this.nbestLength = length;
	}

	BlockingByteQueue gateAudio = new BlockingByteQueue();
	byte[] gateAudioBuffer;
	
	@Override
	public void listenAudio(byte[] buffer, int pos, int len) {
		if (recognizerRunning) {
			activeStream.Write(buffer, pos, len);
			if (inSpeech) {
				if (gateAudio.available() > 0) {
					if (gateAudio.available() % gateAudioBuffer.length != 0) {
						gateAudioBuffer = new byte[gateAudio.available()];
					}
					while (gateAudio.available() > 0) {
						try {
							gateAudio.read(gateAudioBuffer);
							listeners.speechSamples(gateAudioBuffer, 0, gateAudioBuffer.length);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
				listeners.speechSamples(buffer, pos, len);
			} else if (audioPort instanceof AudioGate && ((AudioGate)audioPort).isGateOpen()) {
				gateAudio.write(buffer, pos, len);
				if (gateAudioBuffer == null)
					gateAudioBuffer = new byte[len];
			}
		}
	}

	@Override
	public void startListening() {
	}

	@Override
	public void stopListening() {
	}

	@Override
	public RecognizerFactory getRecognizerFactory() {
		return WindowsRecognizerFactory.INSTANCE;
	}

}
