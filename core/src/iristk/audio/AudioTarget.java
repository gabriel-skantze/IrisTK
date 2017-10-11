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

import org.mozilla.javascript.edu.emory.mathcs.backport.java.util.Arrays;

import iristk.util.BlockingByteQueue;

/**
 * AudioTarget is an AudioListener (typically an output device) which is also an AudioPort, which means that it is possible to connect other listeners to it. 
 */
public abstract class AudioTarget extends AudioPort implements AudioListener {

	private BlockingByteQueue queue;
	private int delay;

	@Override
	public abstract AudioFormat getAudioFormat();
	
	protected abstract void startTarget();
	
	protected abstract void stopTarget();

	protected abstract void writeTarget(byte[] buffer, int pos, int len);
	
	public void setTargetDelay(int bytes) {
		delay = bytes;
		queue = new BlockingByteQueue();
	}
	
	@Override
	public void listenAudio(byte[] buffer, int pos, int len) {
		if (queue != null) {
			queue.write(buffer);
			byte[] targetBuffer = new byte[len];
			if (queue.available() < delay) {
				Arrays.fill(targetBuffer, (byte)0);
			} else {
				try {
					queue.read(targetBuffer);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			writeTarget(targetBuffer, pos, len);
		} else {
			writeTarget(buffer, pos, len);
		}
		writeListeners(buffer, pos, len);
	}
	
	@Override
	public void startListening() {
		startTarget();
		startListeners();
	}

	@Override
	public void stopListening() {
		stopTarget();
		stopListeners();
	}

}
