package iristk.util;

import iristk.audio.AudioChannel;
import iristk.audio.AudioUtil;
import iristk.audio.FileAudioSource;
import iristk.audio.Sound;
import iristk.audio.SoundAudioSource;
import iristk.speech.EndpointerRecognizer;
import iristk.speech.EnergyVAD;
import iristk.speech.EnergyVAD.Listener;
import iristk.speech.RecResult;
import iristk.speech.RecognizerListener;
import iristk.speech.google.GoogleRecognizerProcessor;
import iristk.speech.nuancecloud.NuanceCloudRecognizerListener;
import iristk.speech.prosody.ProsodyData;
import iristk.speech.prosody.ProsodyFeatureExtractor;
import iristk.speech.prosody.ProsodyNormalizer;
import iristk.speech.prosody.ProsodyTracker;
import iristk.system.Event;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ArrayBlockingQueue;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import se.kth.speech.hat.xsd.Annotation;
import se.kth.speech.hat.xsd.Annotation.Segments;
import se.kth.speech.hat.xsd.Annotation.Segments.Segment;
import se.kth.speech.hat.xsd.Annotation.Tracks;
import se.kth.speech.hat.xsd.Annotation.Tracks.Track;
import se.kth.speech.hat.xsd.Annotation.Tracks.Track.Sources;
import se.kth.speech.hat.xsd.Annotation.Tracks.Track.Sources.Source;
import se.kth.speech.hat.xsd.Features;
import se.kth.speech.hat.xsd.Features.Feature;
import se.kth.speech.hat.xsd.Transcription;
import se.kth.speech.hat.xsd.Transcription.T;

public class HAT  {

	public static class RunRecognizer {

		public RunRecognizer(String outputFile, String inputFile, String recognizer, String lang, List<String> trackIds, String asrFeature) throws Exception {
			RecognizerListener listener = null;
			if (recognizer.equals("google")) {
				GoogleRecognizerProcessor google = new GoogleRecognizerProcessor();
				google.setLanguage(new Language(lang));
				listener = google;
			} else if (recognizer.equals("nuance")) {
				NuanceCloudRecognizerListener nuance = new NuanceCloudRecognizerListener();
				nuance.setLanguage(new Language(lang));
				listener = nuance;
			} else {
				throw new IllegalArgumentException("Recognizer " + recognizer + " not known, use 'google' or 'nuance'");
			}
			
			Annotation annotation = readAnnotation(new File(inputFile));
			for (Segment segment : annotation.getSegments().getSegment()) {
				if (trackIds == null || trackIds.contains(segment.getTrack())) {
					Source source = getSource(annotation, segment.getSource());
					Sound sound = getSound(source, segment);
					RecResult result = EndpointerRecognizer.recognizeSound(sound, listener);
					String text = result.getString("text", "").replace(RecResult.NOMATCH, "").trim();
					if (asrFeature == null)
						setText(segment, text);
					else
						setFeature(segment, asrFeature, text);
					System.out.println(segment.getId() + " " + text);
				}
			}
			writeAnnotation(new File(outputFile), annotation);
		}

	}
	
	public static class ProsodyAnalyzer {

		private Annotation annotation;

		private int pos = 0;

		public ProsodyAnalyzer(String outputFile, String inputFile, boolean proFile, List<String> trackIds) throws Exception {
			annotation = readAnnotation(new File(inputFile));

			for (Track track : annotation.getTracks().getTrack()) {
				if (trackIds == null || trackIds.contains(track.getId())) {
					final PrintStream out = proFile ? new PrintStream(outputFile.replace(".xml", "." + track.getId() + ".f0")) : null;
					pos = 0;
					ProsodyNormalizer normalizer = new ProsodyNormalizer();
					for (Segment segment : annotation.getSegments().getSegment()) {
						if (segment.getTrack().equals(track.getId())) {
							if (trackIds == null || trackIds.contains(segment.getTrack())) {
								Source source = getSource(annotation, segment.getSource());
								Sound sound = getSound(source, segment);
								ProsodyTracker tracker = new ProsodyTracker(sound.getAudioFormat());
								SoundAudioSource soundAudioSource = new SoundAudioSource(sound);
								soundAudioSource.addAudioListener(tracker);
								tracker.addProsodyListener(normalizer);
								soundAudioSource.start();
								soundAudioSource.waitFor();
							}
						}
					}
					normalizer.filter(60);
					
					for (Segment segment : annotation.getSegments().getSegment()) {
						if (segment.getTrack().equals(track.getId())) {

							if (out != null) {
								while (pos < (segment.getStart() * 100)) {
									out.println("-100 -1");
									pos++;
								}
							}
							Source source = getSource(annotation, segment.getSource());
							Sound sound = getSound(source, segment);
							ProsodyTracker tracker = new ProsodyTracker(sound.getAudioFormat());
							SoundAudioSource soundAudioSource = new SoundAudioSource(sound);
							soundAudioSource.addAudioListener(tracker);
							
							ProsodyFeatureExtractor analyzer = new ProsodyFeatureExtractor(normalizer);
							
							tracker.addProsodyListener(analyzer);

							soundAudioSource.start();
							soundAudioSource.waitFor();
							
							Record analysis = analyzer.getFeatures();
							
							for (String field : analysis.getFields()) {
								setFeature(segment, field, analysis.getDouble(field));
							}
							
							if (out != null) {
								for (ProsodyData data : analyzer.getData()) {
									pos++;
									if (data.pitch == -1)
										out.println("-50 -1");
									else
										out.println("-50 " + AudioUtil.pitchCentToHz(data.pitch));
								}
							}
						}
					}

				}
			}

			writeAnnotation(new File(outputFile), annotation);
			
		}

	}
	

	private static class MakeFromLog {

		private Annotation annotation;
		private String audioDir;
		private File outputFile;
		private HashSet<String> tracks = new HashSet<String>();
		private File logFile;

		public MakeFromLog(String outputFile, String logFile, String audioDir, String appendFile, Integer offset, List<String> trackIds) throws Exception {
			this.audioDir = audioDir.replace("\\", "/");
			this.logFile = new File(logFile);
			this.outputFile = new File(outputFile);
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(logFile), "utf-8"));
			String line;
			Long startTime = null;
			HashMap<String,Segment> segments = new HashMap<>();
			annotation = new Annotation();
			if (appendFile != null)
				appendAnnotation(annotation, readAnnotation(new File(appendFile)));

			while ((line = br.readLine()) != null) {
				if (line.length() > 0) {
					if (startTime == null) {
						Event event = (Event) Record.fromJSON(line);
						startTime = time(event) - offset;
					}
					if (line.contains("sense.speech.start")) {
						Event event = (Event) Record.fromJSON(line);
						String sensor = event.getString("sensor", "user");
						if (trackIds == null || trackIds.contains(sensor)) {
							String action = event.getString("action") + sensor;
							Segment segment = createSegment(sensor);
							segment.setStart((time(event)-startTime) / 1000f - 0.2f);
							segments.put(action, segment);
						}
					} else if (line.contains("sense.speech.rec")) {
						Event event = (Event) Record.fromJSON(line);
						String sensor = event.getString("sensor", "user");
						String action = event.getString("action") + sensor;
						Segment segment = segments.get(action);
						if (segment != null) {
							segment.setEnd((time(event)-startTime) / 1000f);
							setFeature(segment, "rec.text", event.getString("text", ""));
							Record sem = event.getRecord("sem");
							if (sem != null)
								setFeature(segment, "rec.sem", sem.toJSON().toString());
							String text = event.getString("text", "").replace(RecResult.NOMATCH, "").trim();
							setText(segment, text);
							createTrack(sensor);
							addSegment(annotation, segment);
						}
					} else if (line.contains("action.speech\"")) {
						if (trackIds == null || trackIds.contains("system")) {
							Event event = (Event) Record.fromJSON(line);
							String action = event.getId();
							Segment segment = createSegment("system");
							segments.put(action, segment);
							String text = event.getString("text", "").replaceAll("<.*?>", "").trim();
							setText(segment, text);
						}
					} else if (line.contains("monitor.speech.start")) {
						Event event = (Event) Record.fromJSON(line);
						Segment segment = segments.get(event.getString("action"));
						if (segment != null)
							segment.setStart((time(event)-startTime) / 1000f);
					} else if (line.contains("monitor.speech.end")) {
						Event event = (Event) Record.fromJSON(line);
						Segment segment = segments.get(event.getString("action"));
						if (segment != null) {
							segment.setEnd((time(event)-startTime) / 1000f);
							createTrack("system");
							addSegment(annotation, segment);
						}
					}
				}
			}
			br.close();
			writeAnnotation(new File(outputFile), annotation);
		}

		private Segment createSegment(String track) {
			Segment segment = new Segment();
			segment.setSource(track + "-source");
			segment.setTrack(track);
			return segment;
		}

		private void createTrack(String id) {
			if (!tracks.contains(id)) {
				Source source = new Source();
				source.setId(id + "-source");
				source.setChannel(0);
				source.setHref(audioDir + "/" + id + ".wav");
				addSource(annotation, id, source);
				tracks.add(id);
			}
		}

	}

	private static class MakeWithEndpointer implements Listener {

		private float startOfSpeech = 0;
		private PrintStream console = System.out;
		private FileAudioSource audioSource;
		private float time;
		private ArrayBlockingQueue<Boolean> waitQueue = new ArrayBlockingQueue<>(1);
		private Annotation annotation;
		private float endSil;
		private String trackId;
		private String sourceId;
		private boolean inSpeech;
		private float endSilLength;
		private long lastPos;
		private ArrayList<Segment> segments;

		public MakeWithEndpointer(String outputFile, List<String> inputFiles, List<String> linkFiles, List<String> trackNames, String appendFile, Integer speechThreshold, int endSil) throws Exception {
			if (trackNames != null && trackNames.size() != inputFiles.size())
				throw new Exception("The number of track names must match the number of input files");
			if (linkFiles != null && linkFiles.size() != inputFiles.size())
				throw new Exception("The number of track names must match the number of input files");
			this.endSil = endSil / 1000f;
			annotation = new Annotation();
			if (appendFile != null)
				appendAnnotation(annotation, readAnnotation(new File(appendFile)));
			int trackN = 0;
			List<String> wavFiles = inputFiles;
			for (String wavFile : wavFiles) {
				audioSource = new FileAudioSource(new File(wavFile));
				for (int channel = 0; channel < audioSource.getAudioFormat().getChannels(); channel++) {
					if (trackNames != null)
						trackId = trackNames.get(trackN);
					else
						trackId = "track" + trackN;
					sourceId = trackId + "-source";
					Source source = new Source();
					source.setId(sourceId);
					source.setChannel(channel);
					source.setHref(linkFiles == null ? wavFile : linkFiles.get(trackN));
					addSource(annotation, trackId, source);
					
					console.println(wavFile + "(channel " + channel + ")");

					AudioChannel audioChannel = new AudioChannel(audioSource, channel);

					EnergyVAD vad = new EnergyVAD(audioChannel);
					vad.addVADListener(this);
					
					segments = new ArrayList<>();
					
					//vad.getParameters().deltaSpeech = (energyEndpointerThreshold);
					//vad.getParameters().deltaSil = (energyEndpointerThreshold / 2);
					if (speechThreshold == null) {
						vad.adaptSpeechLevel.set(true);
					} else {
						vad.adaptSpeechLevel.set(false);
						vad.speechLevel.set(speechThreshold);
					}
					inSpeech = false;
					endSilLength = 0;
					lastPos = 0;

					audioSource.start();
					audioSource.waitFor();
					
					// Fix overlapping segments
					for (int i = 0; i < segments.size(); i++) {
						if (i < segments.size()-1 && segments.get(i).getEnd() >= segments.get(i+1).getStart()) {
							segments.get(i+1).setStart(segments.get(i).getStart());
						} else {
							console.println(String.format(Locale.US, "  %.2f-%.2f", segments.get(i).getStart(), segments.get(i).getEnd()));
							addSegment(annotation, segments.get(i));
						}
					}

					trackN++;
					
					console.println("  Silence level: " + vad.getSilenceLevel() + ", Speech threshold: " + vad.getSpeechLevel());
				}
				audioSource.close();
			}
			writeAnnotation(new File(outputFile), annotation);
		}

		@Override
		public void vadEvent(long streamPos, boolean vadSpeech, int energy) {
			if (vadSpeech) {
				if (!inSpeech) {
					startOfSpeech = (streamPos / 16000f) - 0.2f;
					inSpeech = true;
				} 
				endSilLength = 0;
			 }
			 if (inSpeech && !vadSpeech) {
				endSilLength += (streamPos - lastPos) / 16000f;
				if (endSilLength > endSil) {
					float endOfSpeech = (streamPos / 16000f);
					Segment segment = new Segment();
					segment.setStart(startOfSpeech);
					segment.setEnd(endOfSpeech);
					segment.setSource(sourceId);
					segment.setTrack(trackId);
					segments.add(segment);
					inSpeech = false;
				}
			}
			lastPos = streamPos;
		}

	}

	public static void main(String[] args) throws Exception {
		if (args.length == 0) { 
			showUsage();
		} else if (args[0].equals("vad")) {
			ArgParser argParser = new ArgParser();
			argParser.addRequiredArg("i", "Input audio file(s)", "files", List.class);
			argParser.addOptionalArg("l", "Audio file(s) to link", "files", List.class, null);
			argParser.addOptionalArg("n", "Track name(s)", "names", List.class, null);
			argParser.addRequiredArg("o", "Output XML file", "file", String.class);
			argParser.addOptionalArg("a", "Append tracks to file", "file", String.class, null);
			argParser.addOptionalArg("e", "Energy endpointer threshold (default adaptive)", "treshold", Integer.class, null);
			argParser.addOptionalArg("s", "Set the end silence threshold (default 500 msec)", "msec", Integer.class, 500);
			argParser.parse(args, 1, args.length-1);
			new MakeWithEndpointer((String)argParser.get("o"), 
					(List<String>)argParser.get("i"),
					(List<String>)argParser.get("l"),
					(List<String>)argParser.get("n"),
					(String)argParser.get("a"),
					(Integer)argParser.get("e"),
					(Integer)argParser.get("s"));
		} else if (args[0].equals("log")) {
			ArgParser argParser = new ArgParser();
			argParser.addRequiredArg("i", "Input log file", "file", String.class);
			argParser.addRequiredArg("o", "Output XML file", "file", String.class);
			argParser.addOptionalArg("a", "Append tracks to file", "file", String.class, null);
			argParser.addOptionalArg("d", "Audio file directory", "directory", String.class, ".");
			argParser.addOptionalArg("t", "Speaker id:s to create tracks for", "id:s", List.class, null);
			argParser.addOptionalArg("s", "Offset", "msec", Integer.class, 0);
			argParser.parse(args, 1, args.length-1);
			new MakeFromLog((String)argParser.get("o"), 
					(String)argParser.get("i"),
					(String)argParser.get("d"),
					(String)argParser.get("a"),
					(Integer)argParser.get("s"),
					(List<String>)argParser.get("t"));
		} else if (args[0].equals("rec")) {
			ArgParser argParser = new ArgParser();
			argParser.addRequiredArg("i", "Input XML file", "file", String.class);
			argParser.addRequiredArg("o", "Output XML file", "file", String.class);
			argParser.addRequiredArg("r", "Recognizer", "nuance|google", String.class);
			argParser.addOptionalArg("l", "Language", "sv-se|en-us", String.class, "en-us");
			argParser.addOptionalArg("t", "Track id:s to process", "id:s", List.class, null);
			argParser.addOptionalArg("f", "Add as feature", "name", String.class, null);
			argParser.parse(args, 1, args.length-1);
			new RunRecognizer((String)argParser.get("o"), 
					(String)argParser.get("i"),
					(String)argParser.get("r"),
					(String)argParser.get("l"),
					(List<String>)argParser.get("t"),
					(String)argParser.get("f"));
		} else if (args[0].equals("pro")) {
			ArgParser argParser = new ArgParser();
			argParser.addRequiredArg("i", "Input XML file", "file", String.class);
			argParser.addRequiredArg("o", "Output XML file", "file", String.class);
			argParser.addBooleanArg("p", "Output prosody extraction");
			argParser.addOptionalArg("t", "Track id:s to process", "id:s", List.class, null);
			argParser.parse(args, 1, args.length-1);
			new ProsodyAnalyzer((String)argParser.get("o"), 
					(String)argParser.get("i"),
					(Boolean)argParser.get("p"),
					(List<String>)argParser.get("t"));
		} else {
			showUsage();
		}
	}


	public static Sound getSound(Source source, Segment segment) throws Exception {
		return new Sound(new File(source.getHref()), segment.getStart(), segment.getEnd() - segment.getStart(), source.getChannel());
	}

	public static void appendAnnotation(Annotation toAnnotation, Annotation fromAnnotation) {
		if (toAnnotation.getTracks() == null)
			toAnnotation.setTracks(new Tracks());
		for (Track track : fromAnnotation.getTracks().getTrack()) {
			toAnnotation.getTracks().getTrack().add(track);
		}
		for (Segment segment : fromAnnotation.getSegments().getSegment()) {
			addSegment(toAnnotation, segment);
		}
	}

	/**
	 * Adds a segment to an annotation and makes sure that the segment gets a unique id
	 */
	public static void addSegment(Annotation annotation, Segment segment) {
		if (annotation.getSegments() == null)
			annotation.setSegments(new Segments());
		int id = 0;
		String ids = segment.getId();
		if (ids == null)
			ids = "segment" + id;
		SEARCH_ID:
			while (true) {
				for (Segment seg : annotation.getSegments().getSegment()) {
					if (seg.getId().equals(ids)) {
						ids = "segment" + id++;
						continue SEARCH_ID;
					}
				}
				segment.setId(ids);
				break SEARCH_ID;
			}
		for (int i = 0; i < annotation.getSegments().getSegment().size(); i++) {
			Segment seg = annotation.getSegments().getSegment().get(i);
			if (segment.getStart() < seg.getStart()) {
				annotation.getSegments().getSegment().add(i, segment);
				return;
			}
		}
		annotation.getSegments().getSegment().add(segment);
	}

	public static Long time(Event event) {
		return Timestamp.valueOf(event.getTime()).getTime();
	}

	private static void showUsage() {
		System.out.println("Commands:");
		System.out.println("iristk hat vad     Create tracks with a voice activity detector");
		System.out.println("iristk hat log     Create tracks based on IrisTK log files");
		System.out.println("iristk hat rec     Run Nuance Cloud Recognizer on segments");
		System.out.println("iristk hat pro     Generate prosodic features for segments");
		System.exit(0);
	}

	public static Annotation readAnnotation(File file) throws JAXBException {
		JAXBContext jc = JAXBContext.newInstance("se.kth.speech.hat.xsd");
		Unmarshaller unmarshaller = jc.createUnmarshaller();
		Annotation annotation = (Annotation) unmarshaller.unmarshal(file);
		return annotation;
	}

	public static void writeAnnotation(File file, Annotation annotation) throws JAXBException {
		JAXBContext jc = JAXBContext.newInstance("se.kth.speech.hat.xsd");
		Marshaller marshaller = jc.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_ENCODING, "iso-8859-1");
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.marshal(annotation, file);
	}

	public static void addSource(Annotation annotation, String trackId, Source source) {
		Track track = new Track();
		track.setId(trackId);
		if (track.getSources() == null)
			track.setSources(new Sources());
		track.getSources().getSource().add(source);
		if (annotation.getTracks() == null)
			annotation.setTracks(new Tracks());
		annotation.getTracks().getTrack().add(track);
	}

	public static String getText(Segment segment) {
		String result = "";
		for (Object o : segment.getTranscription().getSegmentOrT()) {
			if (o instanceof T) {
				String t = ((T)o).getContent();
				result += t + " ";
			}
		}
		return result.trim();
	}

	public static void setText(Segment segment, String text) {
		Transcription trans = new Transcription();
		segment.setTranscription(trans);
		if (text != null && text.length() > 0) {
			for (String word : text.split(" ")) {
				if (word.length() == 0)	continue;
				T t = new T();
				t.setContent(word);
				trans.getSegmentOrT().add(t);
			}
		}
	}

	public static Source getSource(Annotation annotation, String id) {
		for (Track track : annotation.getTracks().getTrack()) {
			for (Source source : track.getSources().getSource()) {
				if (source.getId().equals(id))
					return source;
			}
		}
		return null;
	}

	public static void setFeature(Segment segment, String name, double value) {
		setFeature(segment, name, String.format(Locale.US, "%.2f", value));
	}
	
	public static void setFeature(Segment segment, String name, String value) {
		if (segment.getFeatures() == null) 
			segment.setFeatures(new Features());
		for (Feature feature : segment.getFeatures().getFeature()) {
			if (feature.getName().equals(name)) {
				feature.getContent().clear();
				feature.getContent().add(value);
				return;
			}
		}
		Feature feature = new Feature();
		feature.setName(name);
		feature.getContent().add(value);
		segment.getFeatures().getFeature().add(feature);
	}

	public static boolean hasFeature(Segment segment, String name) {
		if (segment.getFeatures() != null) {
			for (Feature feature : segment.getFeatures().getFeature()) {
				if (feature.getName().equals(name)) {
					return true;
				}
			}
		}
		return false;
	}

	public static String getFeature(Segment segment, String name) {
		if (segment.getFeatures() != null) {
			for (Feature feature : segment.getFeatures().getFeature()) {
				if (feature.getName().equals(name)) {
					List<Object> contents = feature.getContent();
					if (contents.size() > 0) {
						return contents.get(0).toString();
					}
				}
			}
		}
		return null;
	}

}
