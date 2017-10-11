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
package iristk.audio;

import javax.sound.sampled.AudioFormat;

public class SoundPlayer {
	
	private boolean playing;
	
	private Thread playThread;
	private AudioTarget audioTarget;
	private AudioFormat audioFormat;

	private int pos;

	public SoundPlayer(AudioFormat format) {
		this(new Speaker(format));
	}
	
	public SoundPlayer(AudioTarget audioTarget) {
		this.audioTarget = audioTarget;
		this.audioFormat = audioTarget.getAudioFormat();
	}
	
	public void stop() {
		playing = false;
		if (isPlaying()) {
			try {
				playThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public int play(Sound sound, int msecPos, int msecLength) {
		if (msecPos < 0)
			msecPos = 0;
		playing = true;
		int startPos = (int) (msecPos * (audioFormat.getSampleRate() / 1000) * audioFormat.getFrameSize());
		int endPos;
		if (msecLength < 0)
			endPos = sound.getBytes().length;
		else
			endPos = startPos + (int) (msecLength
					* (audioFormat.getSampleRate() / 1000) * audioFormat.getFrameSize());
		pos = startPos;
		int frameSize = 320;
		int len = frameSize;
		audioTarget.startListening();
		PLAYING: {
			for (; pos < endPos; pos += frameSize) {
				if (endPos - pos < frameSize)
					len = endPos - pos;
				audioTarget.listenAudio(sound.getBytes(), pos, len);
				if (!playing)
					break PLAYING;
			}
			//audioTarget.flush();
		}
		audioTarget.stopListening();
		playing = false;
		return pos;
	}
	
	public int getSamplePosition() {
		return pos / (audioFormat.getFrameSize());
	}
	
	public boolean isPlaying() {
		return (playThread != null && playThread.isAlive());
	}
	
	public int play(Sound sound, int msecPos) {
		return play(sound, msecPos, -1);
	}

	public int play(Sound sound) {
		return play(sound, 0);
	}
	
	public void playAsync(Sound sound) {
		playAsync(sound, 0, null);
	}

	public void playAsync(Sound sound, final CallbackDelegate callback) {
		playAsync(sound, 0, callback);
	}

	public void playAsync(Sound sound, final int msecPos) {
		playAsync(sound, msecPos, null);
	}

	public void playAsync(Sound sound, int start, int length) {
		playAsync(sound, start, length, null);
	}

	public void playAsync(final Sound sound, final int msecPos, final CallbackDelegate callback) {
		playAsync(sound, msecPos, -1, callback);
	}
	
	public synchronized void playAsync(final Sound sound, final int msecPos, final int length, final CallbackDelegate callback) {
		stop();
		playThread = new Thread(new Runnable() {
			@Override
			public void run() {
				int endPos = play(sound, msecPos, length);
				if (callback != null)
					callback.callback(endPos);
			}
		});
		playThread.setName("SoundPlayer");
		playThread.start();
	}
	
	public void waitForPlayingDone() {
		try {
			playThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public interface CallbackDelegate {
		public void callback(int endPos);
	}

	public AudioFormat getAudioFormat() {
		return audioFormat;
	}
	
	public AudioTarget getAudioTarget() {
		return audioTarget;
	}
	
}
