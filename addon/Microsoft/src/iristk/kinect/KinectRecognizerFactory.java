package iristk.kinect;

import iristk.audio.AudioPort;
import iristk.speech.Recognizer;
import iristk.speech.RecognizerException;
import iristk.speech.RecognizerFactory;
import iristk.system.InitializationException;
import iristk.util.Language;

public class KinectRecognizerFactory extends RecognizerFactory {

	@Override
	public Recognizer newRecognizer(AudioPort audioPort) throws RecognizerException {
		try {
			return new KinectRecognizer(audioPort);
		} catch (InitializationException e) {
			throw new RecognizerException(e.getMessage());
		}
	}

	@Override
	public String getName() {
		return "Kinect Recognizer";
	}

	@Override
	public Language[] getSupportedLanguages() {
		//TODO: check for real
		return new Language[]{Language.ENGLISH_US};
	}

	@Override
	public Class<? extends Recognizer> getRecognizerClass() {
		return KinectRecognizer.class;
	}
	
	@Override
	public boolean supportsOpenVocabulary() {
		return false;
	}

	@Override
	public boolean requiresInternet() {
		return false;
	}

}
