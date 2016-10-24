package iristk.speech.nuancecloud;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import iristk.audio.AudioPort;
import iristk.speech.Recognizer;
import iristk.speech.RecognizerException;
import iristk.speech.RecognizerFactory;
import iristk.util.Language;

public class NuanceCloudRecognizerFactory extends RecognizerFactory {

	private NuanceCloudLicense license;

	@Override
	public Recognizer newRecognizer(AudioPort audioPort) throws RecognizerException {
		return new NuanceCloudRecognizer(audioPort, license);
	}

	@Override
	public void checkSupported() throws RecognizerException {
		checkLicense(license);
		//TODO: test internet connection
	}
	
	public static void checkLicense(NuanceCloudLicense license) throws RecognizerException {
		if (license == null && !NuanceCloudPackage.PACKAGE.getPath("license.properties").exists()) {
			throw new RecognizerException("No license found");
		}
	}
	
	@Override
	public String getName() {
		return "Nuance Cloud";
	}

	public void setLicense(String appId, String appKey) {
		this.license = new NuanceCloudLicense(appId, appKey);
	}

	@Override
	public Language[] getSupportedLanguages() {
		//TODO: should really check which are supported
		return new Language[]{Language.ENGLISH_US, Language.SWEDISH};
	}

	@Override
	public Class<? extends Recognizer> getRecognizerClass() {
		return NuanceCloudRecognizer.class;
	}

	@Override
	public boolean requiresInternet() {
		return true;
	}
	
	public static void main(String[] args) {
		try {
			// This does not work
			System.out.println(InetAddress.getByName("dictation.nuancemobility.net").isReachable(3000));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
