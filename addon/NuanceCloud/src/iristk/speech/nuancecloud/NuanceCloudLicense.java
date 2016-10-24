package iristk.speech.nuancecloud;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class NuanceCloudLicense {

	private String appId;
	private String appKey;

	public NuanceCloudLicense(String appId, String appKey) {
		this.appId = appId;
		this.appKey = appKey;
	}

	public static NuanceCloudLicense read() throws IOException {
		Properties properties = new Properties();
		properties.load(new FileReader(NuanceCloudPackage.PACKAGE.getPath("license.properties")));
		String appId = properties.getProperty("APP_ID");
		String appKey = properties.getProperty("APP_KEY");
		return new NuanceCloudLicense(appId, appKey);
	}
	
	public String getAppId() {
		return appId;
	}

	public String getAppKey() {
		return appKey;
	}

}
