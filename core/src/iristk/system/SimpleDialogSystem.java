package iristk.system;

import java.io.File;

import iristk.agent.face.FaceModule;
import iristk.audio.AudioLogger;
import iristk.audio.Microphone;
import iristk.speech.Console;
import iristk.speech.Recognizer;
import iristk.speech.RecognizerFactory;
import iristk.speech.RecognizerModule;
import iristk.speech.Synthesizer;
import iristk.speech.SynthesizerModule;
import iristk.speech.Voice.Gender;

public class SimpleDialogSystem extends AbstractDialogSystem {

	private RecognizerModule recognizerModule;
	private SynthesizerModule synthesizerModule;
	private Console console;
	private FaceModule faceModule;
	
	public SimpleDialogSystem(String name, File path) throws Exception {
		super(name, path);
	}
	
	public SimpleDialogSystem(Class<?> packageClass) throws Exception {
		super(packageClass);
	}
	
	public void setupRecognizer(RecognizerFactory recognizerFactory) throws Exception {
		onlyOnce("Recognizer");
		recognizerFactory.checkSupported();
		Microphone mic = new Microphone();
		Recognizer recognizer = recognizerFactory.newRecognizer(mic);
		recognizerModule = new RecognizerModule(recognizer);
		addModule(recognizerModule);
		if (loggingModule != null && recognizer.getAudioPort() != null) {
			loggingModule.addLogger(new AudioLogger("user", mic, true));
		}
		addVADPanel(recognizer);
	}

	public void setupConsoleRecognizer() throws InitializationException {
		recognizerModule = new RecognizerModule(console.getRecognizer());
		addModule(recognizerModule);
	}
	
	public void setupFace() throws Exception {
		if (synthesizerModule == null) {
			throw new Exception("Synthesizer has to be setup before the face");
		}
		Gender gender = synthesizerModule.getCurrentVoice().getGender();
		setupFace(gender == Gender.FEMALE ? "female" : "male");
	}
	
	public void setupFace(String faceModelName) throws Exception {
		if (synthesizerModule == null) {
			throw new Exception("Synthesizer has to be setup before the face");
		}
		faceModule = new FaceModule(faceModelName);
		addModule(faceModule);
		synthesizerModule.doLipsync(true);		
	}
	
	public void setupSynthesizer(Synthesizer synthesizer, Gender gender) throws Exception {
		onlyOnce("Synthesizer");
		synthesizerModule = new SynthesizerModule(synthesizer);
		addModule(synthesizerModule);
		if (gender == null)
			synthesizerModule.setVoice(getLanguage());
		else
			synthesizerModule.setVoice(getLanguage(), gender);
		if (loggingModule != null)
			loggingModule.addLogger(new AudioLogger("system", synthesizerModule.getAudioTarget(), true));
	}
	
	public void setupSynthesizer(Synthesizer synthesizer) throws Exception {
		setupSynthesizer(synthesizer, null);
	}

	public Console getConsole() {
		return console;
	}

	public RecognizerModule getRecognizerModule() {
		return recognizerModule;
	}
	
	public SynthesizerModule getSynthesizerModule() {
		return synthesizerModule;
	}
	
	public FaceModule getFaceModule() {
		return faceModule;
	}

}

