package iristk.speech.windows;

import iristk.audio.AudioPort;
import iristk.speech.Recognizer;
import iristk.speech.RecognizerException;
import iristk.speech.RecognizerFactory;
import iristk.speech.nuancecloud.NuanceCloudLicense;
import iristk.speech.nuancecloud.NuanceCloudRecognizerFactory;
import iristk.system.InitializationException;
import iristk.util.Language;

public class WindowsNuanceCloudRecognizerFactory extends RecognizerFactory {

	private NuanceCloudLicense license;

	@Override
	public Recognizer newRecognizer(AudioPort audioPort) throws RecognizerException {
		WindowsNuanceCloudRecognizer recognizer;
		try {
			recognizer = new WindowsNuanceCloudRecognizer(audioPort, license);
		} catch (InitializationException e) {
			throw new RecognizerException(e.getMessage());
		}
		return recognizer;
	}

	@Override
	public void checkSupported() throws RecognizerException {
		NuanceCloudRecognizerFactory.checkLicense(license);
	}

	@Override
	public String getName() {
		return "Windows + Nuance Cloud";
	}

	public void setLicense(String appId, String appKey) {
		this.license = new NuanceCloudLicense(appId, appKey);
	}

	@Override
	public Language[] getSupportedLanguages() {
		return WindowsRecognizerFactory.INSTANCE.getSupportedLanguages();
	}

	@Override
	public Class<? extends Recognizer> getRecognizerClass() {
		return WindowsNuanceCloudRecognizer.class;
	}

	@Override
	public boolean requiresInternet() {
		return true;
	}

}
