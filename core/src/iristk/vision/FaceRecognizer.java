package iristk.vision;

import java.io.File;
import java.util.Collection;

public interface FaceRecognizer {

	/** 
	 * Starts asynchronous recognition of an image. The result will be reported to the face recognizer listeners
	 */
	void recognize(String id, File imageFile);
	
	/** 
	 * Add a face recognizer listener that will receive the result of the recognition
	 */
	void addFaceRecognizerListener(FaceRecognizerListener listener);

	/**
	 * Adds a face image to a (new or old) person to be used for training
	 */
	void addFace(String name, File imageFile);

	/**
	 * Starts (asynchronous) training of the enrolled persons and faces
	 */
	void train();
	
	/** 
	 * Returns a list of the names of the persons currently enrolled
	 */
	Collection<String> getNames();

}
