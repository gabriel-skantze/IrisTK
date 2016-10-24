package iristk.audio;

import iristk.util.BlockingByteQueue;

import java.io.IOException;
import java.io.InputStream;

public class AudioInputStream extends InputStream implements AudioListener {

	BlockingByteQueue queue = new BlockingByteQueue();
	
	public AudioInputStream(AudioPort audioPort) {
		audioPort.addAudioListener(this);
	}
	
	public InputStream getInputStream() {
		return this;
	}

	@Override
	public void listenAudio(byte[] buffer, int pos, int len) {
		if (reading)
			queue.write(buffer, pos, len);
	}

	@Override
	public void startListening() {
	}

	@Override
	public void stopListening() {
	}

	byte[] bytes = new byte[1];
	private boolean reading = false;
	
	@Override
	public int read() throws IOException {
		try {
			queue.read(bytes);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return bytes[0];
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		try {
			return queue.read(b, off, len);
		} catch (InterruptedException e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	public void startReading() {
		queue.reset();
		reading  = true;
	}
	
	public void stopReading() {
		reading = false;
	}
	
}
