package iristk.speech;

import iristk.audio.AudioPort;

import javax.sound.sampled.AudioFormat;

/**
 * The Endpointer listens for speech and generates empty recognition results. 
 */
public interface Endpointer {

	/**
	 * Tells the recognizer to start listen
	 * @throws RecognizerException
	 */
	void startListen() throws RecognizerException;
	
	/**
	 * Forces the recognizer to stop listening. It should be possible to call this function even if 
	 * the recognizer is not in the listening state. The method should block until the stopping is 
	 * complete and the recognizer is ready to start listening again. 
	 * @return true if the recognizer was listening, false otherwise. 
	 * @throws RecognizerException
	 */
	boolean stopListen() throws RecognizerException;
	
	/**
	 * Sets the maximum length of silence before the recognizer stops and gives a NOSPEECH result
	 * @param msec the number of milliseconds for the timeout
	 * @throws RecognizerException
	 */
	void setNoSpeechTimeout(int msec) throws RecognizerException;
	
	/**
	 * Sets the end silence threshold for detecting an end of utterance
	 * @param msec the number of milliseconds for the timeout
	 * @throws RecognizerException
	 */
	void setEndSilTimeout(int msec) throws RecognizerException;
	
	/**
	 * Sets the maximum length of an utterance before the recognizer stops and gives a MAXSPEECH result
	 * @param msec the number of milliseconds for the timeout
	 * @throws RecognizerException
	 */
	void setMaxSpeechTimeout(int msec) throws RecognizerException;
	
	/**
	 * Adds a listener for recognizer events
	 * @param listener
	 * @param priority The listeners with the lowest priority will be called first
	 */
	void addRecognizerListener(RecognizerListener listener, int priority);
	
	/**
	 * @return The audio format that the recognizer operates on
	 */
	AudioFormat getAudioFormat();
	
	/**
	 * @return The audio port used for the endpointer
	 */
	AudioPort getAudioPort();

}
