package iristk.audio;

import javax.sound.sampled.AudioFormat;

public class SoundAudioSource extends AudioSource {

	private Sound sound;
	private int soundpos = 0;

	public SoundAudioSource(Sound sound) {
		this.sound = sound;
	}
	
	@Override
	protected void startSource() {
		soundpos = 0;
	}

	@Override
	protected void stopSource() {
	}

	@Override
	protected int readSource(byte[] buffer, int pos, int len) {
		if (soundpos < sound.getBytes().length) {
			if (soundpos + len > sound.getBytes().length) {
				len = sound.getBytes().length - soundpos;
			}
			System.arraycopy(sound.getBytes(), soundpos, buffer, pos, len);
			soundpos += len;
			return len;
		} else {
			return -1;
		}
	}

	@Override
	public AudioFormat getAudioFormat() {
		return sound.getAudioFormat();
	}

}
