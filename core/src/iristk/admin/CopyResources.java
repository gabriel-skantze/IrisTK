package iristk.admin;

import java.io.File;
import java.io.IOException;
import java.util.*;

import iristk.util.Utils;

public class CopyResources {
	
	public static void distribute(String src, String target) throws IOException {
		File srcFile = new File(src).getCanonicalFile();
		System.out.println("Copying " + srcFile);
		List<String> files = new ArrayList<>();
		Utils.listFiles(target, files, srcFile.getName());
		for (String f : files) {
			File dest = new File(f).getCanonicalFile();
			if (!dest.equals(srcFile)) {
				System.out.println("to " + dest);
				Utils.copyFile(srcFile, dest);
			}
		}
	}
	
	public static void distribute(String file) throws IOException {
		distribute(file, "C:/Dropbox/iristk/app");
		distribute(file, "C:/Dropbox/iristk/addon");
		distribute(file, "C:/Dropbox/iristk/core");
		distribute(file, "C:/Dropbox/iristk/doc");
		distribute(file, "C:/Dropbox/iristk/templates");
		distribute(file, "C:/Dropbox/Furhat/FurhatOS/templates");
		distribute(file, "C:/Dropbox/Furhat/FurhatOS/app");
		distribute(file, "C:/Dropbox/Furhat/FurhatOS/addon");
	}
	
	public static void main(String[] args) throws IOException {
		distribute("core/xml/flow.xsd");
		//distribute("core/src/iristk/situated/SystemAgentFlow.xsd");
	}

}
