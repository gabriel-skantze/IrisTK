/*******************************************************************************
 * Copyright (c) 2014 Gabriel Skantze.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Gabriel Skantze - initial API and implementation
 ******************************************************************************/
package iristk.speech;

import java.io.File;

/**
 * The Recognizer listens for speech and generates recognition results. 
 * Unlike the parent class Endpointer, it actually performs speech recognition.
 * The things it should listen for can be controlled with Contexts (by activating and deactivating them). 
 */
public interface Recognizer extends Endpointer {
	
	/**
	 * Recognizes a wave-file
	 * @param file the file to recognize
	 * @return a recognition result
	 * @throws RecognizerException
	 */
	RecResult recognizeFile(File file) throws RecognizerException;

	/**
	 * Sets whether to generate partial results (default should be false)
	 */
	void setPartialResults(boolean cond);
	
	/**
	 * Specifies how many alternative results to return (default 1) 
	 */
	void setNbestLength(int length);
	
	/**
	 * Activates a speech recognition Context 
	 */
	void activateContext(String name, float weight) throws RecognizerException;
	
	/**
	 * Deactivates a speech recognition Context 
	 */
	void deactivateContext(String name) throws RecognizerException;
	
	/**
	 * Returns the RecognizerFactory that generated this Recognizer
	 */
	RecognizerFactory getRecognizerFactory();

}
