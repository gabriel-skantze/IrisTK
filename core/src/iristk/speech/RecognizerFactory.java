package iristk.speech;

import iristk.audio.AudioPort;
import iristk.util.Language;

public abstract class RecognizerFactory {

	/**
	 * Returns a new Recognizer that listens to the specified AudioPort
	 */
	public abstract Recognizer newRecognizer(AudioPort audioPort) throws RecognizerException;

	/**
	 * Checks whether the Recognizer is supported by the system
	 */
	public void checkSupported() throws RecognizerException {
	}
	
	/**
	 * Returns a list of languages supported by the recognizer
	 */
	public abstract Language[] getSupportedLanguages();
	
	/**
	 * Returns the class of the Recognizer that this factory generates
	 */
	public abstract Class<? extends Recognizer> getRecognizerClass();
	
	/**
	 * Returns a pretty name for this Recognizer 
	 */
	public abstract String getName();
	
	/**
	 * Returns whether this recognizer requires an internet connection 
	 */
	public abstract boolean requiresInternet();

	/**
	 * Checks if this Recognizer supports the specified Language
	 */
	public boolean supportsLanguage(Language language) {
		for (Language lang : getSupportedLanguages()) {
			if (lang.equals(language)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks if this Recognizer supports speech grammars
	 */
	public boolean supportsSpeechGrammar() {
		return GrammarRecognizer.class.isAssignableFrom(getRecognizerClass());
	}
	
	/**
	 * Checks if this Recognizer supports open vocabulary recognition
	 */
	public boolean supportsOpenVocabulary() {
		return OpenVocabularyRecognizer.class.isAssignableFrom(getRecognizerClass());
	}

}
