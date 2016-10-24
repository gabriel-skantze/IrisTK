package iristk.speech.nuance9;

import iristk.audio.AudioPort;
import iristk.project.Launcher;
import iristk.speech.Recognizer;
import iristk.speech.RecognizerException;
import iristk.speech.RecognizerFactory;
import iristk.util.Language;

public class NuanceRecognizerFactory extends RecognizerFactory {

	@Override
	public Recognizer newRecognizer(AudioPort audioPort) throws RecognizerException {
		return new NuanceRecognizer(audioPort);
	}

	@Override
	public void checkSupported() throws RecognizerException {
		if (Launcher.is64arch())
			throw new RecognizerException("Nuance 9 only runs in 32-bit mode");
	}

	@Override
	public String getName() {
		return "Nuance 9";
	}

	@Override
	public Language[] getSupportedLanguages() {
		//TODO: should really check which languages are installed
		return new Language[]{Language.ENGLISH_US};
	}

	@Override
	public Class<? extends Recognizer> getRecognizerClass() {
		return NuanceRecognizer.class;
	}

	@Override
	public boolean requiresInternet() {
		return false;
	}

}
