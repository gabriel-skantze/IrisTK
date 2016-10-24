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

/**
 * AudioSource acts as an input AudioPort which contains a thread that actively reads audio from a port.
 * To access this audio, one or more AudioListeners must be attached. 
 */
public abstract class AudioSource extends AudioPort implements Runnable {

	private Thread runningThread = null;
	private boolean contRunning;
	private int bufferSize;
	
	protected abstract void startSource();
	
	protected abstract void stopSource();
	
	protected abstract int readSource(byte[] buffer, int pos, int len);
	
	@Override
	public void run() {
		startSource();
		startListeners();
		// 100 frames per second
		bufferSize = (int) ((getAudioFormat().getSampleRate() * getAudioFormat().getFrameSize()) / 100);
		byte[] frame = new byte[bufferSize];
		while (contRunning) {
			int read = readSource(frame, 0, frame.length);
			if (read == -1) {
				contRunning = false;
			} else {
				writeListeners(frame, 0, read);
			}
		}
		stopSource();
		stopListeners();
	}
	
	public void start() {
		contRunning = true;
		runningThread = new Thread(this);
		runningThread.start();
	}
	
	public void stop() {
		contRunning = false;
		try {
			runningThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void waitFor() {
		try {
			runningThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}
