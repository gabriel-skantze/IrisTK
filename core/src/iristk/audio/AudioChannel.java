package iristk.audio;

import javax.sound.sampled.AudioFormat;

/**
 * An AudioChannel listens to a multi-channel AudioPort and provides a new (mono) AudioPort which only contains one specified channel.
 */
public class AudioChannel extends AudioPort {

	public static final int LEFT = 0;
	public static final int RIGHT = 1;
	
	private byte[] cframe = new byte[0];
	private int channel;
	private AudioPort port;
	private AudioFormat audioFormat;
	
	public AudioChannel(AudioPort port, int channel) {
		this.channel = channel;
		this.port = port;
		this.audioFormat = AudioUtil.setChannels(port.getAudioFormat(), 1);
		port.addAudioListener(new MyAudioListener());
	}
	
	private class MyAudioListener implements AudioListener {
 
		@Override
		public void listenAudio(byte[] frame, int pos, int len) {
			int nChannels = port.getAudioFormat().getChannels();
			int clen = len / nChannels;
			if (cframe.length != clen)
				cframe = new byte[clen];
			int cpos = 0;
			for (int i = 0; i < len; i += nChannels * 2) {
				cframe[cpos] = frame[i + channel * 2];
				cframe[cpos+1] = frame[i + channel * 2 + 1];
				cpos += 2;
			}
			writeListeners(cframe, 0, clen);
		}
	
		@Override
		public void startListening() {
			startListeners();
		}
	
		@Override
		public void stopListening() {
			stopListeners();
		}
		
	}

	@Override
	public AudioFormat getAudioFormat() {
		return audioFormat;
	}
	
	@Override
	public String getDeviceName() {
		return port.getDeviceName();
	}

}
