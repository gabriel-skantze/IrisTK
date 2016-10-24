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
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import java.io.*;

public class FileAudioSource extends AudioSource {

	private AudioInputStream audioInputStream;
	private int readPos = 0;
	private boolean open = true;
	private File file;

	public FileAudioSource(File file) throws UnsupportedAudioFileException, IOException {
		this.audioInputStream = AudioSystem.getAudioInputStream(file);
		this.file = file;
	}

	@Override
	public AudioFormat getAudioFormat() {
		return audioInputStream.getFormat();
	}

	@Override
	protected void startSource() {
		try {
			this.audioInputStream = AudioSystem.getAudioInputStream(file);
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void stopSource() {
		close();
	}
	
	public synchronized void close() {
		if (open) {
			try {
				audioInputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			open = false;
		}
	}

	@Override
	protected int readSource(byte[] buffer, int pos, int len) {
		int nBytesRead = -1;
		try {
			nBytesRead = audioInputStream.read(buffer, pos, len);
			if (nBytesRead == -1) {
				close();
			} else {
				readPos += nBytesRead;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return nBytesRead;
	}
	
	public int getPosition() {
		return readPos;
	}
	
}
