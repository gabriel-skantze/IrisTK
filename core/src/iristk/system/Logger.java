package iristk.system;

import java.io.File;
import java.io.IOException;

public interface Logger {
	 
	void startLogging(File file) throws IOException;
	
	void stopLogging() throws IOException;
	
}
