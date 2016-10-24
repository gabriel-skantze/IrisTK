package iristk.speech.windows;

import iristk.audio.AudioPort;
import iristk.net.speech.DesktopRecognizer;
import iristk.speech.Recognizer;
import iristk.speech.RecognizerException;
import iristk.speech.RecognizerFactory;
import iristk.system.InitializationException;
import iristk.util.Language;

public class WindowsRecognizerFactory extends RecognizerFactory {

	public static final WindowsRecognizerFactory INSTANCE = new WindowsRecognizerFactory();
	
	@Override
	public Recognizer newRecognizer(AudioPort audioPort) throws RecognizerException {
		WindowsRecognizer recognizer;
		try {
			recognizer = new WindowsRecognizer(audioPort);
		} catch (InitializationException e) {
			throw new RecognizerException(e.getMessage());
		}
		return recognizer;
	}

	@Override
	public void checkSupported() throws RecognizerException {
	}

	@Override
	public String getName() {
		return "Windows";
	}

	@Override
	public Language[] getSupportedLanguages() {
		String langstr = DesktopRecognizer.getLanguages();
		if (langstr.trim().length() == 0)
			return new Language[0];
		String[] langs = langstr.split(" ");
		Language[] supportedLanguages = new Language[langs.length];
		for (int i = 0; i < langs.length; i++) {
			supportedLanguages[i] = new Language(langs[i]);
		}
		return supportedLanguages;
	}

	@Override
	public Class<? extends Recognizer> getRecognizerClass() {
		return WindowsRecognizer.class;
	}

	@Override
	public boolean requiresInternet() {
		return false;
	}

}
