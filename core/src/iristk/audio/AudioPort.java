package iristk.audio;

import java.util.ArrayList;

import javax.sound.sampled.AudioFormat;

/**
 * An AudioPort is an object through which audio passes (either as input or output) 
 * and to which it is possible to connect one or more AudioListeners. 
 */
public abstract class AudioPort {

	private ArrayList<AudioListener> listeners = new ArrayList<AudioListener>();
	private boolean listenersRunning = false;
	
	public abstract AudioFormat getAudioFormat();
	
	public String getDeviceName() {
		return "unknown";
	}
	
	public synchronized void addAudioListener(AudioListener listener) {
		listeners.add(listener);
		if (listenersRunning)
			listener.startListening();
	}
	
	public synchronized void removeAudioListener(AudioListener listener) {
		listeners.remove(listener);
	}
	
	protected synchronized void writeListeners(byte[] frame, int pos, int len) {
		for (AudioListener listener : listeners) {
			listener.listenAudio(frame, pos, len);
		}
	}

	protected synchronized void startListeners() {
		listenersRunning  = true;
		for (AudioListener target : listeners) {
			target.startListening();
		}
	}

	protected synchronized void stopListeners() {
		listenersRunning = false;
		for (AudioListener target : listeners) {
			target.stopListening();
		}
	}
	
}
