package iristk.audio;

import java.util.Arrays;

import javax.sound.sampled.AudioFormat;

import iristk.util.BlockingByteQueue;

public class DelayedAudioListener implements AudioListener {

	private AudioListener target;
	private BlockingByteQueue queue = new BlockingByteQueue();

	public DelayedAudioListener(AudioListener target, AudioFormat format, int delay) {
		this.target = target;
		byte[] buffer = new byte[AudioUtil.secondLengthToBytes(format, delay/1000f)];
		Arrays.fill(buffer, (byte)0);
		queue.write(buffer);
	}

	@Override
	public void listenAudio(byte[] buffer, int pos, int len) {
		queue.write(buffer, pos, len);
		try {
			queue.read(buffer, pos, len);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		target.listenAudio(buffer, pos, len);
	}

	@Override
	public void startListening() {
		target.startListening();
	}

	@Override
	public void stopListening() {
		target.stopListening();
	}

}
