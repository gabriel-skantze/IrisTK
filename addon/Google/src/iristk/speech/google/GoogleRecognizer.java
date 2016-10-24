package iristk.speech.google;

import iristk.audio.AudioPort;
import iristk.speech.EndpointerRecognizer;
import iristk.speech.EnergyEndpointer;
import iristk.speech.EnergyVAD;
import iristk.speech.EnergyVADContainer;
import iristk.speech.OpenVocabularyContext;
import iristk.speech.OpenVocabularyRecognizer;
import iristk.speech.RecResult;
import iristk.speech.RecognizerException;
import iristk.speech.RecognizerFactory;
import iristk.speech.RecognizerListeners;
import iristk.system.IrisUtils;
import iristk.util.Language;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.slf4j.Logger;

public class GoogleRecognizer extends EndpointerRecognizer implements OpenVocabularyRecognizer, EnergyVADContainer {
	
	private static Logger logger = IrisUtils.getLogger(GoogleRecognizer.class);
	
	private GoogleRecognizerProcessor google;
	private HashSet<String> activeContexts = new HashSet<>();
	private HashMap<String,OpenVocabularyContext> loadedContexts = new HashMap<>();
	
	public GoogleRecognizer(AudioPort audioPort, File credentials) throws RecognizerException {
		super(new EnergyEndpointer(audioPort));
		google = new GoogleRecognizerProcessor(credentials);
		addRecognizerListener(google, RecognizerListeners.PRIORITY_RECOGNIZER);
	}

	@Override
	public RecResult recognizeFile(File file) throws RecognizerException {
		return EndpointerRecognizer.recognizeFile(file, google);
	}
	
	@Override
	public void setPartialResults(boolean cond) {
		google.setPartialResults(cond);
	}
	
	@Override
	public void setNbestLength(int length) {
		google.setNbestLength(length);
	}

	@Override
	public void activateContext(String name, float weight) throws RecognizerException {
		activeContexts.add(name);
		checkActive();
	}

	@Override
	public void deactivateContext(String name) throws RecognizerException {
		activeContexts.remove(name);
		checkActive();
	}

	private void checkActive() {
		List<String> phrases = new ArrayList<>();
		boolean active = false;
		Language lang = null;
		for (String contextName : loadedContexts.keySet()) {
			if (activeContexts.contains(contextName)) {
				google.setActive(true);
				OpenVocabularyContext context = loadedContexts.get(contextName);
				if (lang == null)
					lang = context.language;
				else if (!lang.equals(context.language))
					logger.error("Conflicting languages in context '" + context.name + "': " + lang + " and " + context.language);
				if (context.phrases != null)
					phrases.addAll(context.phrases);
				active = true;
			}
		}
		if (active) {
			google.setLanguage(lang);
			google.setPhrases(phrases);
			google.setActive(true);
		} else {
			google.setActive(false);
		}
	}

	@Override
	public void loadOpenVocabulary(String contextName, OpenVocabularyContext context) throws RecognizerException {
		loadedContexts.put(contextName, context);
	}

	@Override
	public void unloadOpenVocabulary(String contextName) throws RecognizerException {
		loadedContexts.remove(contextName);
	}

	@Override
	public EnergyVAD getEnergyVAD() {
		if (getEndpointer() instanceof EnergyEndpointer) {
			return ((EnergyEndpointer)getEndpointer()).getEnergyVAD();
		} else {
			return null;
		}
	}

	@Override
	public RecognizerFactory getRecognizerFactory() {
		return new GoogleRecognizerFactory();
	}
}
