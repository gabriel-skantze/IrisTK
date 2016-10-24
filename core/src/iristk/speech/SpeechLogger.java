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
package iristk.speech;

import iristk.util.BlockingByteQueue;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

public class SpeechLogger implements RecognizerListener {

	private AudioFormat format;
	private BlockingByteQueue buffer = new BlockingByteQueue();

	@Override
	public void recognitionResult(RecResult result) {
		if (buffer.available() > 0) {
			try {
				AudioSystem.write(new AudioInputStream(buffer.getInputStream(), format, buffer.available() / format.getFrameSize()), AudioFileFormat.Type.WAVE, new File("c:/test.wav"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void speechSamples(byte[] samples, int pos, int len) {
		buffer.write(samples, pos, len);
	}

	@Override
	public void initRecognition(AudioFormat audioFormat) {
		buffer.reset();
		this.format = audioFormat;
	}

	@Override
	public void startOfSpeech(float timestamp) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void endOfSpeech(float timestamp) {
		// TODO Auto-generated method stub
		
	}

	
}
