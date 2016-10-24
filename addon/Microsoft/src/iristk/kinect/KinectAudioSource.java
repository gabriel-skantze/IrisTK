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
package iristk.kinect;

import iristk.audio.AudioSource;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;

public class KinectAudioSource extends AudioSource {

	private final IKinect kinect;
	private final AudioFormat format;
	private boolean running = false;
	
	public KinectAudioSource(IKinect kinectSensor) {
		this.kinect = kinectSensor;
		this.format = new AudioFormat(Encoding.PCM_SIGNED, 16000, 16, 1, 2, 16000, false);
		start();
	}
	
	@Override
	public AudioFormat getAudioFormat() {
		return format;
	}

	@Override
	protected void startSource() {
		kinect.startAudioStream();
		running = true;
	}

	@Override
	protected void stopSource() {
		running = false;
		kinect.stopAudioStream();
	}

	@Override
	protected int readSource(byte[] buffer, int pos, int len) {
		if ( !running ) return 0;
		
		byte[] readData = kinect.readAudioStream(len);
		
		if (readData == null)
			return 0;
		
		int readLength = readData.length;
		
		System.arraycopy(readData, 0, buffer, pos, readLength);
		
		return readLength;
	}
}
