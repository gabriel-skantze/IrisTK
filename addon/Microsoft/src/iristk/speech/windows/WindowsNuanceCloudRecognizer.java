package iristk.speech.windows;

import java.util.HashMap;
import java.util.HashSet;

import iristk.audio.AudioPort;
import iristk.speech.OpenVocabularyContext;
import iristk.speech.RecResult;
import iristk.speech.RecognizerException;
import iristk.speech.RecognizerListeners;
import iristk.speech.ResultPolicy;
import iristk.speech.nuancecloud.NuanceCloudLicense;
import iristk.speech.nuancecloud.NuanceCloudRecognizerListener;
import iristk.system.InitializationException;
import iristk.util.Language;

public class WindowsNuanceCloudRecognizer extends WindowsRecognizer {
	
	//private double threshold = 0.9;
	private HashSet<String> activeContexts = new HashSet<>();
	private HashMap<String,Language> loadedContexts = new HashMap<>();
	private NuanceCloudRecognizerListener nuance;

	//public void setConfThreshold(double threshold) {
	//	this.threshold  = threshold;
	//}
	
	public WindowsNuanceCloudRecognizer(AudioPort audioPort) throws InitializationException {
		this(audioPort, null);
	}
	
	public WindowsNuanceCloudRecognizer(AudioPort audioPort, NuanceCloudLicense license) throws InitializationException {
		super(audioPort);
		nuance = new NuanceCloudRecognizerListener(license);
		nuance.setResultPolicy(new ResultPolicy() {
			@Override
			public boolean isReady(RecResult result) {
				return (result.isFinal() && !result.isNomatch() && !loadedContexts.containsKey(result.grammar)); 
			}
		});
		addRecognizerListener(nuance, RecognizerListeners.PRIORITY_SECONDARY_RECOGNIZER);
	}
	
	@Override
	public void activateContext(String name, float weight) throws RecognizerException {
		super.activateContext(name, weight);
		activeContexts.add(name);
		checkActive();
	}

	@Override
	public void deactivateContext(String name) throws RecognizerException {
		super.deactivateContext(name);
		activeContexts.remove(name);
		checkActive();
	}

	private void checkActive() {
		for (String context : loadedContexts.keySet()) {
			if (activeContexts.contains(context)) {
				nuance.setActive(true);
				nuance.setLanguage(loadedContexts.get(context));
				return;
			}
		}
		nuance.setActive(false);
	}
	
	@Override
	public void loadOpenVocabulary(String contextName, OpenVocabularyContext context) throws RecognizerException {
		super.loadOpenVocabulary(contextName, context);
		loadedContexts.put(contextName, context.language);
	}

	@Override
	public void unloadOpenVocabulary(String contextName) throws RecognizerException {
		super.unloadOpenVocabulary(contextName);
		loadedContexts.remove(contextName);
	}
	
}
