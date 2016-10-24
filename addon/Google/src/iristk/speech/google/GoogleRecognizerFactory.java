package iristk.speech.google;

import java.io.File;

import iristk.audio.AudioPort;
import iristk.project.Launcher;
import iristk.project.Project;
import iristk.speech.Recognizer;
import iristk.speech.RecognizerException;
import iristk.speech.RecognizerFactory;
import iristk.speech.nuancecloud.NuanceCloudRecognizer;
import iristk.util.Language;

public class GoogleRecognizerFactory extends RecognizerFactory {

	private static Language[] languages;

	private File credentials = Project.main.getPackage(GooglePackage.NAME).getPath("credentials.json");
	
	static {
		String[] codes = Language.getCodes();
		languages = new Language[codes.length];
		int i = 0;
		for (String code : codes) {
			languages[i++] = new Language(code);
		}
	}
	
	@Override
	public Recognizer newRecognizer(AudioPort audioPort) throws RecognizerException {
		return new GoogleRecognizer(audioPort, credentials);
	}

	public void setCredentials(File file) {
		this.credentials = file;
	}
	
	@Override
	public void checkSupported() throws RecognizerException {
		if (!Launcher.is64arch())
			throw new RecognizerException("Google Recognizer only runs in 64-bit mode");
		if (!credentials.exists())
			throw new RecognizerException("Google Recognizer has no credentials");
		//TODO: test internet connection
	}
	
	@Override
	public String getName() {
		return "Google";
	}

	@Override
	public Language[] getSupportedLanguages() {
		return languages;
	}

	@Override
	public Class<? extends Recognizer> getRecognizerClass() {
		return NuanceCloudRecognizer.class;
	}

	@Override
	public boolean requiresInternet() {
		return true;
	}

}
