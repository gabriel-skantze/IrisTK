package iristk.vision;

public interface FaceRecognizerListener {

	/** 
	 * The face was recognized
	 */
	void faceRecognized(String id, String name, double confidence);
	
	/** 
	 * The face recognition failed
	 */
	void faceUnknown(String id);
	
}
