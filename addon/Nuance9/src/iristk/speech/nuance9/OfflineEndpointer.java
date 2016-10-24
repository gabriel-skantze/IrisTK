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
package iristk.speech.nuance9;

import iristk.speech.RecognizerListener;
import iristk.speech.nuance9.SWIep.SWIepAudioSamples;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;


import com.sun.jna.Memory;
import com.sun.jna.WString;
import com.sun.jna.ptr.IntByReference;

public class OfflineEndpointer extends BaseRecognizer {

	public OfflineEndpointer() throws NuanceException {
		super();
	}

	private int bufferSize = 1600;
	
	public void endpointWav(String file, RecognizerListener eh) throws IOException, NuanceException {
		AudioInputStream audioInputStream;
		try {
			audioInputStream = AudioSystem.getAudioInputStream(new File(file));
		} catch (UnsupportedAudioFileException e) {
			throw new IOException(e.getMessage());
		}
		String encoding;
		try {
			encoding = BaseRecognizer.getEncoding(audioInputStream.getFormat());
		} catch (IllegalArgumentException e) {
			throw new IOException(e.getMessage());
		}
		
		ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
		int bufferSize = 1024;
		int	nBytesRead = 0;
		int pos = 0;
		byte[] buffer = new byte[bufferSize];
		while (true) {
			nBytesRead = audioInputStream.read(buffer, 0, bufferSize);
			if (nBytesRead == -1) 
				break;
			dataStream.write(buffer, 0, nBytesRead);
			pos += nBytesRead;
		}
		byte[] data = dataStream.toByteArray();	
		audioInputStream.close();	
		endpointData(encoding, data, eh);
	}
		
	public void endpointData(String encoding, byte[] data, RecognizerListener eh) throws NuanceException {
		setEpParameter("swiep_mode", "begin_end");
		setEpParameter("maxspeechtimeout", "30000");
		setEpParameter("timeout", "30000");
		setEpParameter("incompletetimeout", "100");
		
		bufferSize = 800;

		IntByReference state = new IntByReference();
		IntByReference beginSample = new IntByReference();
		IntByReference endSample = new IntByReference();
		SWIepAudioSamples epSamples = new SWIepAudioSamples();
		epSamples.type = new WString(encoding);
		epSamples.samples = new Memory(bufferSize);
		epSamples.len = bufferSize;
		int lastEnd = 0;
		
		OUTER:
		while (true) {

			epStart();
			epPromptDone();

			boolean first = true;
			boolean speechStart = false;
			
			for (int i = lastEnd * 2; i < data.length; i += bufferSize) {
				
				if (data.length - i < bufferSize)  {
					break OUTER;
				}
				
				epSamples.samples.write(0, data, i, bufferSize);
				if (first)
					epSamples.status = SWIrec.SWIrec_SAMPLE_FIRST;
				else
					epSamples.status = SWIrec.SWIrec_SAMPLE_CONTINUE;
				epWrite(epSamples, state, beginSample, endSample);
	
				if (state.getValue() == SWIep.SWIep_IN_SPEECH) {
					if (!speechStart) {
						eh.startOfSpeech((800 + lastEnd + beginSample.getValue()) / 8000);
						speechStart = true;
					}
				} else if (state.getValue() == SWIep.SWIep_AFTER_SPEECH) {
					eh.endOfSpeech((lastEnd + endSample.getValue() - 800) / 8000);
					lastEnd += (endSample.getValue() - 800);
					break;
				} else if (state.getValue() == SWIep.SWIep_TIMEOUT) {
					break;
				} else if (state.getValue() == SWIep.SWIep_MAX_SPEECH) {
					break;
				}  
				first = false;
			}

			epStop();
			
		}
	
		epStop();
		epAcousticStateReset();
		
	}
	
}
