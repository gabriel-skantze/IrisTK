package iristk.situated;

import java.io.File;

import iristk.agent.face.FaceModule;
import iristk.audio.AudioChannel;
import iristk.audio.AudioLogger;
import iristk.audio.AudioPort;
import iristk.audio.Microphone;
import iristk.audio.MicrophoneConfiguration;
import iristk.flow.FlowModule;
import iristk.kinect.CameraViewPanel;
import iristk.kinect.KinectAudioSource;
import iristk.kinect.KinectCameraView;
import iristk.kinect.KinectModule;
import iristk.kinect.KinectRecognizerModule;
import iristk.speech.EnergyVADContainer;
import iristk.speech.Recognizer;
import iristk.speech.RecognizerFactory;
import iristk.speech.RecognizerModule;
import iristk.speech.Synthesizer;
import iristk.speech.SynthesizerModule;
import iristk.speech.Voice.Gender;
import iristk.system.AbstractDialogSystem;
import iristk.system.InitializationException;
import iristk.project.Package;

public class SituatedDialogSystem extends AbstractDialogSystem {

	private Situation situation;
	private KinectModule kinectModule;
	private SynthesizerModule synth;
	private SituationPanel topPanel;
	private SituationPanel sidePanel;
	private SystemAgent systemAgent;
	private File staticFilePath;

	public SituatedDialogSystem(String name) throws Exception {
		super(name);
	}
	
	public SituatedDialogSystem(String name, Package pack) throws Exception {
		super(name, pack);
	}
	
	public SituatedDialogSystem(String name, File path) throws Exception {
		super(name, path);
	}
	
	public SituatedDialogSystem(Class<?> packageClass) throws Exception {
		super(packageClass);
	}
	

	/**
	 * Adds a default system agent with the name "system"
	 * @return The created SystemAgentModule
	 */
	public SystemAgentFlow addSystemAgent() throws InitializationException {
		return addSystemAgent("system");
	}
	
	/**
	 * Adds a system agent with a specified name
	 * @return The created SystemAgentFlow
	 */
	public SystemAgentFlow addSystemAgent(String name) throws InitializationException {
		onlyOnce("Agent '" + name + "'");
		SystemAgentModule systemAgentModule = new SystemAgentModule(name, staticFilePath);
		systemAgent = systemAgentModule.getSystemAgent();
		situation = systemAgent.getSituation();
		systemAgent.setInteractionDistance(2);
		addModule(systemAgentModule);
		SystemAgentFlow situatedDialogFlow = new SystemAgentFlow(systemAgent);
		addModule(new FlowModule("agentflow-" + name, situatedDialogFlow));
		return situatedDialogFlow;
	}
	
	@Override
	public void setupGUI() throws InitializationException {
		super.setupGUI();
		// Add a panel to the GUI with a top view of the situation
		topPanel = new SituationPanel(getGUI(), this, SituationPanel.TOPVIEW);
		sidePanel = new SituationPanel(getGUI(), this, SituationPanel.SIDEVIEW);
	}

	public void setupKinect() throws InitializationException {
		onlyOnce("Kinect");
		
		// Add a Kinect module to the system
		kinectModule = new KinectModule();
		addModule(kinectModule);

		// Set the kinect position. Default is below/front of the monitor/face
		kinectModule.setPosition(new Location(0, -0.35, 0.15), new Rotation(347, 0, 0));
		
		if (getGUI() != null) {
			// Add the Kinect camera view to the situation 
			new CameraViewPanel(getGUI(), kinectModule.getKinect()).setDecorator(new KinectCameraView(kinectModule));
		}
	}
	
	/*
	public void setupRealSense() throws InitializationException {
		onlyOnce("RealSense");
		
		// Add a Kinect module to the system
		realSenseModule = new RealSenseModule();
		addModule(realSenseModule);

		// Set the kinect position. Default is below/front of the monitor/face
		realSenseModule.setPosition(new Location(0, -0.35, 0.15), new Rotation(347, 0, 0));
		
		if (gui != null) {
			new RealSenseCameraPanel(gui, realSenseModule);
		}
	}
	*/

	public void setupFace(String faceModelName, Synthesizer synthesizer, Gender gender, String agentName) throws Exception {
		onlyOnce("Face '" + agentName + "'");
		FaceModule face = new FaceModule(faceModelName);
		face.setAgentName(agentName);
		addModule(face);
		synth = new SynthesizerModule(synthesizer);
		synth.setAgentName(agentName);
		// Turn on lipsync since we are using an animated agent. 
		synth.doLipsync(true);		
		addModule(synth);
		if (gender == null)
			synth.setVoice(getLanguage());
		else
			synth.setVoice(getLanguage(), gender);
		if (loggingModule != null)
			loggingModule.addLogger(new AudioLogger("system", synth.getAudioTarget(), true));
	}
	
	public void setupFace(String faceModelName, Synthesizer synthesizer) throws Exception {
		setupFace(faceModelName, synthesizer, null, "system");
	}
	
	public void setupFace(Synthesizer synthesizer, Gender gender) throws Exception {
		setupFace(gender == Gender.FEMALE ? "female" : "male", synthesizer, gender, "system");
	}
	
	public void setupFace(Synthesizer synthesizer, Gender gender, String agentName) throws Exception {
		setupFace(gender == Gender.FEMALE ? "female" : "male", synthesizer, gender, agentName);
	}
	
	public void setupMicrophones(RecognizerFactory recognizerFactory, MicrophoneConfiguration config) throws Exception {
		if (!config.isDone())
			throw new Exception("Microphone configuration incomplete");
		if (config.channels.size() == 1) {
			Recognizer rec = setupMonoMicrophone(recognizerFactory, config.channels.get(0).device);
			if (rec instanceof EnergyVADContainer) {
				((EnergyVADContainer)rec).getEnergyVAD().speechLevel.set(config.channels.get(0).speechMean);
			}
		} else if (config.channels.size() == 2) {
			Recognizer[] rec = setupStereoMicrophones(recognizerFactory, config.channels.get(0).device, config.channels.get(1).device);
			for (int i = 0; i < 2; i++) {
				if (rec[i] instanceof EnergyVADContainer) {
					((EnergyVADContainer)rec[i]).getEnergyVAD().speechLevel.set(config.channels.get(i).speechMean);
				}	
			}
		}
	}
	
	public Recognizer[] setupStereoMicrophones(RecognizerFactory recognizerFactory) throws Exception {
		return setupStereoMicrophones(recognizerFactory, null);
	}
	
	public Recognizer[] setupStereoMicrophones(RecognizerFactory recognizerFactory, String stereoDeviceName) throws Exception {
		recognizerFactory.checkSupported();
		Microphone stereo = new Microphone(stereoDeviceName, 16000, 2);
		AudioPort leftChannel = new AudioChannel(stereo, 0);
		AudioPort rightChannel = new AudioChannel(stereo, 1);
		return setupStereoMicrophones(recognizerFactory, leftChannel, rightChannel);
	}
	
	public Recognizer[] setupStereoMicrophones(RecognizerFactory recognizerFactory, String leftDeviceName, String rightDeviceName) throws Exception {
		AudioPort leftChannel = new Microphone(leftDeviceName, 16000, 1);
		AudioPort rightChannel = new Microphone(rightDeviceName, 16000, 1);
		return setupStereoMicrophones(recognizerFactory, leftChannel, rightChannel);
	}
	
	public Recognizer[] setupStereoMicrophones(RecognizerFactory recognizerFactory, AudioPort leftChannel, AudioPort rightChannel) throws Exception {
		onlyOnce("Recognizer");
		recognizerFactory.checkSupported();
		RecognizerModule recognizerLeft = new RecognizerModule(
				recognizerFactory.newRecognizer(leftChannel));
		RecognizerModule recognizerRight = new RecognizerModule(
				recognizerFactory.newRecognizer(rightChannel));
		recognizerLeft.setSensor(new LeftRightSensor("mic-left", LeftRightSensor.LEFT), true);
		recognizerRight.setSensor(new LeftRightSensor("mic-right", LeftRightSensor.RIGHT), true);
		addModule(recognizerLeft);
		addModule(recognizerRight);
		if (loggingModule != null) {
			loggingModule.addLogger(new AudioLogger("mic-left", leftChannel, true));
			loggingModule.addLogger(new AudioLogger("mic-right", rightChannel, true));
		}
		addVADPanel(recognizerLeft.getRecognizer(), recognizerRight.getRecognizer());
		return new Recognizer[]{recognizerLeft.getRecognizer(), recognizerRight.getRecognizer()};
	}

	public Recognizer setupMonoMicrophone(RecognizerFactory recognizerFactory) throws Exception {
		return setupMonoMicrophone(recognizerFactory, null);
	}
	
	public Recognizer setupMonoMicrophone(RecognizerFactory recognizerFactory, String deviceName) throws Exception {
		onlyOnce("Recognizer");
		recognizerFactory.checkSupported();
		Microphone mic = new Microphone(deviceName);
		RecognizerModule recognizer = new RecognizerModule(recognizerFactory.newRecognizer(mic));
		//Associate the recognizer with the microphone
		recognizer.setSensor(new Sensor("mic"), true);
		// Add the microphone to the situation model so that the microphone can be associated with a user
		addModule(recognizer);
		if (loggingModule != null) {
			loggingModule.addLogger(new AudioLogger("user", mic, true));
		}
		addVADPanel(recognizer.getRecognizer());
		return recognizer.getRecognizer();
	}
	
	public Recognizer setupKinectMicrophone(RecognizerFactory recognizerFactory) throws Exception {
		onlyOnce("Recognizer");
		recognizerFactory.checkSupported();
		// Use the Kinect sensor for speech recognition 
		RecognizerModule recognizer = new KinectRecognizerModule(kinectModule, recognizerFactory.newRecognizer(new KinectAudioSource(kinectModule.getKinect())));
		addModule(recognizer);
		addVADPanel(recognizer.getRecognizer());
		return recognizer.getRecognizer();
	}

	public KinectModule getKinectModule() {
		return kinectModule;
	}

	public Situation getSituation() {
		return situation;
	}

	public void loadPositions(File positionsFile) {
		if (topPanel != null) {
			topPanel.setPositionsFile(positionsFile);
			sidePanel.setPositionsFile(positionsFile);
		}
		if (positionsFile.exists()) {
			SituationModule.loadPositions(this, positionsFile);
		}
	}
	
	/**
	 * @param staticFolder Path to the static folder of the system
	 */
	public void setStaticFilePath(File staticFolder) {
		this.staticFilePath = staticFolder;
	}

}
