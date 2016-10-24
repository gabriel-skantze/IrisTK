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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import com.sun.jna.Memory;
import com.sun.jna.WString;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

import iristk.audio.AudioGate;
import iristk.audio.AudioListener;
import iristk.audio.AudioPort;
import iristk.audio.AudioUtil;
import iristk.audio.Microphone;
import iristk.speech.RecResult;
import iristk.speech.nuance9.SWIep.SWIepAudioSamples;
import iristk.speech.nuance9.SWIrec.SWIrecAudioSamples;
import iristk.speech.GrammarRecognizer;
import iristk.speech.Recognizer;
import iristk.speech.RecognizerException;
import iristk.speech.RecognizerFactory;
import iristk.speech.RecognizerListener;
import iristk.speech.RecognizerListeners;
import iristk.system.InitializationException;
import iristk.util.BlockingByteQueue;
import iristk.util.Language;

public class NuanceRecognizer implements Recognizer, GrammarRecognizer, AudioListener {

	private AudioPort audioPort;
	private BaseRecognizer recognizer;
	private boolean running = false;
	private int bufferSize = 1600;
	BlockingByteQueue speechBytes = new BlockingByteQueue();
	private boolean cancel = false;
	private int endSilTimeout;
	private ListeningThread listeningThread;
	RecognizerListeners listeners = new RecognizerListeners();
	private BlockingByteQueue audioData = new BlockingByteQueue();

	public NuanceRecognizer() throws RecognizerException, InitializationException {
		this(new Microphone(8000, 1));
	}
	
	public NuanceRecognizer(AudioPort audioPort) throws RecognizerException {
		this.audioPort = audioPort;
		audioPort.addAudioListener(this);
		try {
			recognizer = new BaseRecognizer();
		} catch (NuanceException e) {
			throw new RecognizerException(e.getMessage());
		}
	}
	
	public void makeWords(boolean cond) {
		recognizer.makeWords(cond);
	}
	
	@Override
	public void loadGrammar(String name, Language language, URI uri) throws RecognizerException {
		try {
			recognizer.loadGrammar(name, uri);
		} catch (NuanceException e) {
			throw new RecognizerException(e.getMessage());
		}
	}
	
	@Override
	public void loadGrammar(String name, Language language, String grammarString) throws RecognizerException {
		try {
			recognizer.loadGrammar(name, grammarString);
		} catch (NuanceException e) {
			throw new RecognizerException(e.getMessage());
		}
	}
	
	@Override
	public void activateContext(String name, float weight) throws RecognizerException {
		try {
			recognizer.activateGrammar(name, (int)weight);
		} catch (NuanceException e) {
			throw new RecognizerException(e.getMessage());
		}
	}
	
	@Override
	public void deactivateContext(String name) throws RecognizerException {
		try {
			recognizer.deactivateGrammar(name);
		} catch (NuanceException e) {
			throw new RecognizerException(e.getMessage());
		}
	}


	@Override
	public void unloadGrammar(String name) throws RecognizerException {
		try {
			recognizer.unloadGrammar(name);
		} catch (NuanceException e) {
			throw new RecognizerException(e.getMessage());
		}
	}

	
	@Override
	public boolean stopListen() throws RecognizerException {
		try {
			if (running || recognizer.isRunning()) {
				//recognizer.log("stopListen: running");
				cancel = true;
				recognizer.stopRecognize();
				if (running) {
					listeningThread.join();
				}
				return true;
			} else {
				//recognizer.log("stopListen: NOT running");
				return false;
			}
		} catch (InterruptedException e) {
			throw new RecognizerException(e.getMessage());
		}
	}

	@Override
	public void setNoSpeechTimeout(int msec) throws RecognizerException {
		recognizer.setEpParameter("timeout", new Integer(msec).toString());
	}
	
	@Override
	public void setEndSilTimeout(int msec) throws RecognizerException {
		endSilTimeout = msec;
		recognizer.setRecParameter("incompletetimeout", new Integer(msec).toString());
		recognizer.setRecParameter("completetimeout", new Integer(msec).toString());
	}

	@Override
	public void setMaxSpeechTimeout(int msec) {
		recognizer.setRecParameter("maxspeechtimeout", new Integer(msec).toString());
	}
	
	@Override
	public void setNbestLength(int length) {
		recognizer.setRecParameter("swirec_nbest_list_length", new Integer(length).toString());
		recognizer.makeNbest(length > 1);
	}
	
	@Override
	public void startListen() throws RecognizerException {
		if (audioPort instanceof AudioGate)
			((AudioGate)audioPort).closeGate();
		//recognizer.log("startListen");
		// Added this stop, don't know if it is needed
		stopListen();
		listeningThread = new ListeningThread();
		listeningThread.start();
	}

	private class ListeningThread extends Thread {
		
		public ListeningThread() {
			super("ListeningThread");
		}
		
		@Override
		public void run() {
			try {
				listen();
			} catch (NuanceException e) {
				e.printStackTrace();
			}
		}
	}

	public void listen() throws NuanceException {
		if (!running) {
			audioData.reset();
			running  = true;
			
			bufferSize = 1600;
			
			byte[] readBuffer = new byte[bufferSize];
			byte[] readBuffer16 = new byte[bufferSize * 2];
			byte[] writeBuffer = new byte[bufferSize];
			IntByReference state = new IntByReference();
			IntByReference beginSample = new IntByReference();
			IntByReference endSample = new IntByReference();
			SWIepAudioSamples epSamples = new SWIepAudioSamples();
			epSamples.type = new WString(BaseRecognizer.getEncoding(audioPort.getAudioFormat()));
			epSamples.samples = new Memory(bufferSize);
			epSamples.len = bufferSize;
			SWIrecAudioSamples recSamples = new SWIrecAudioSamples();
			recSamples.type = new WString(BaseRecognizer.getEncoding(audioPort.getAudioFormat()));
			recSamples.samples = new Memory(bufferSize);
			boolean first = true;
			boolean speechStart = false;
			cancel = false;
			
			//boolean resample = audioPort.getAudioFormat().getFrameRate() != 8000;
			speechBytes.reset();
			int writePos = 0;
			//byte[] speechBuffer = (resample ? new byte[bufferSize * 2] : new byte[bufferSize]);
			byte[] speechBuffer = new byte[bufferSize];
			
			listeners.initRecognition(audioPort.getAudioFormat());
			
			recognizer.startRecognize();
			
			recognizer.epStart();
			recognizer.epPromptDone();
			while (!cancel) {
				//TODO: should be possible to resample from any rate
				if (audioPort.getAudioFormat().getFrameRate() == 16000) {
					try {
						audioData.read(readBuffer16, 0, readBuffer16.length);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					AudioUtil.resample(readBuffer16, audioPort.getAudioFormat(), readBuffer, 8000);
				} else {
					try {
						audioData.read(readBuffer, 0, readBuffer.length);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				speechBytes.write(readBuffer);
				writePos += readBuffer.length;
				epSamples.samples.write(0, readBuffer, 0, bufferSize);
				if (first)
					epSamples.status = SWIrec.SWIrec_SAMPLE_FIRST;
				else
					epSamples.status = SWIrec.SWIrec_SAMPLE_CONTINUE;
				recognizer.epWrite(epSamples, state, beginSample, endSample);
								
				if (state.getValue() == SWIep.SWIep_IN_SPEECH) {
					if (!speechStart) {
						listeners.startOfSpeech(beginSample.getValue() / audioPort.getAudioFormat().getFrameRate());
						try {
							speechBytes.skip(beginSample.getValue() * 2);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						speechStart = true;
					}
					if (speechBytes.available() >= speechBuffer.length) {
						try {
							speechBytes.read(speechBuffer);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						listeners.speechSamples(speechBuffer, 0, speechBuffer.length);
					}
					recognizer.epRead(recSamples, state, bufferSize);
					while (recSamples.len > 0) {
						recognizer.recAudioWrite(recSamples);
						recSamples.samples.read(0, writeBuffer, 0, bufferSize);
						recognizer.epRead(recSamples, state, bufferSize);
					}
					if (!recognizer.isRunning()) {
						listeners.endOfSpeech(((writePos / 16f) - endSilTimeout) / 1000f);
						//recognizer.log("X: " + recognizer.getResult().type);
						if (!cancel)
							listeners.recognitionResult(recognizer.getResult());
						break;
					}
				} else if (state.getValue() == SWIep.SWIep_TIMEOUT) {
					//recognizer.log("Timeout");
					recognizer.stopRecognize();
					if (!cancel)
						listeners.recognitionResult(new NuanceResult(RecResult.SILENCE));
					break;
				} else if (state.getValue() == SWIep.SWIep_MAX_SPEECH) {
					recognizer.stopRecognize();
					if (!cancel)
						listeners.recognitionResult(new NuanceResult(RecResult.MAXSPEECH));
					break;
				} else if (state.getValue() == SWIep.SWIep_LOOKING_FOR_SPEECH) {
				} else {
					System.err.println("Nuance unknown result: " + state.getValue());
				}
				first = false;
			}
			recognizer.epStop();
			
			running = false;
		} 
	}

	@Override
	public RecResult recognizeFile(File file) throws RecognizerException {
		try {
			recognizer.acousticStateReset();
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
			String encoding = BaseRecognizer.getEncoding(audioInputStream.getFormat());
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
			if (audioInputStream.getFormat().getSampleRate() != 8000) {
				byte[] data8 = new byte[(data.length / 4) * 2];
				AudioUtil.resample(data, audioInputStream.getFormat(), data8, 8000);
				data = data8;
			}
			audioInputStream.close();	
			return recognizeAudioBytes(encoding, data);
		} catch (NuanceException e) {
			throw new RecognizerException(e.getMessage());
		} catch (UnsupportedAudioFileException e) {
			throw new RecognizerException(e.getMessage());
		} catch (IOException e) {
			throw new RecognizerException(e.getMessage());
		}
	}
		
	public NuanceResult recognizeAudioBytes(String encoding, byte[] data) throws NuanceException {
		recognizer.recRecognizerStart();
		
		SWIrecAudioSamples samples = new SWIrecAudioSamples();
		samples.type = new WString(encoding);
		samples.samples = new Memory(bufferSize);
		
		for (int i = 0; i < data.length; i += bufferSize) {
			samples.len = bufferSize;
			if (i == 0) {
				samples.status = SWIrec.SWIrec_SAMPLE_FIRST;
			} else if ((data.length - i) <= bufferSize) {
				samples.status = SWIrec.SWIrec_SAMPLE_LAST;
				samples.len = data.length - i;
			} else {
				samples.status = SWIrec.SWIrec_SAMPLE_CONTINUE;
			}
			samples.samples.write(0, data, i, samples.len);
			recognizer.recAudioWrite(samples);
		}

		IntByReference status = new IntByReference();
		IntByReference type = new IntByReference();
		PointerByReference result = new PointerByReference();
		
		do {
			recognizer.recRecognizerCompute(-1, status, type, result);
			//if (debug)
			//	System.out.println("status: " + status.getValue() + ", type: " + type.getValue());
		} while (status.getValue() > 1);
		
		if (status.getValue() == 0)
			return new NuanceResult(result.getValue(), true, false);
		else
			return new NuanceResult(RecResult.FINAL, RecResult.NOMATCH);
	}

	@Override
	public void addRecognizerListener(RecognizerListener listener, int priority) {
		listeners.add(listener, priority);
	}

	@Override
	public void setPartialResults(boolean cond) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public AudioFormat getAudioFormat() {
		return audioPort.getAudioFormat();
	}

	@Override
	public void listenAudio(byte[] buffer, int pos, int len) {
		if (running) {
			audioData.write(buffer, pos, len);
		}
	}

	@Override
	public void startListening() {
	}

	@Override
	public void stopListening() {
	}

	@Override
	public AudioPort getAudioPort() {
		return audioPort;
	}

	@Override
	public RecognizerFactory getRecognizerFactory() {
		return new NuanceRecognizerFactory();
	}

}
