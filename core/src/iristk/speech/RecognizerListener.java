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

import javax.sound.sampled.AudioFormat;

/**
 * A listener that receives recognition events. The RecognizerListener might also modify the recognition results, 
 * and thereby act as a processor in a pipeline for example to do parsing or prosodic analysis). 
 * However, the listener cannot generate new (partial) results, or do any other modifications to the events. 
 * For that, use RecognizerProcessor instead. 
 */
public interface RecognizerListener {

	/**
	 * Called first at the start of a new recognition. 
	 */
	void initRecognition(AudioFormat format);
	
	/**
	 * A start of speech event
	 * @param timestamp the number of seconds since listening started
	 */
	void startOfSpeech(float timestamp);

	/**
	 * An end of speech event
	 * @param timestamp the number of seconds since listening started
	 */
	void endOfSpeech(float timestamp);

	/**
	 * Speech samples (called between startOfSpeech and endOfSpeech)
	 */
	void speechSamples(byte[] samples, int pos, int len);

	/**
	 * A recognition result. A RecResult should always generated at least once, 
	 * even if no speech was detected (in which case the type will be of RecResult.SILENCE). 
	 * If partial results are allowed, it might also be called several times during recognition (with type RecResult.PARTIAL).  
	 */
	void recognitionResult(RecResult result);

}
