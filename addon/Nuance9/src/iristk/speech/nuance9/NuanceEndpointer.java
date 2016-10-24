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

import javax.sound.sampled.AudioFormat;

import iristk.audio.AudioGate;
import iristk.audio.AudioListener;
import iristk.audio.AudioPort;
import iristk.audio.AudioUtil;
import iristk.audio.Microphone;
import iristk.speech.Endpointer;
import iristk.speech.RecResult;
import iristk.speech.RecognizerException;
import iristk.speech.nuance9.SWIep.SWIepAudioSamples;
import iristk.speech.RecognizerListener;
import iristk.speech.RecognizerListeners;
import iristk.system.InitializationException;
import iristk.util.BlockingByteQueue;

import com.sun.jna.Memory;
import com.sun.jna.WString;
import com.sun.jna.ptr.IntByReference;

public class NuanceEndpointer implements Endpointer, AudioListener {

	private boolean running = false;
	private int bufferSize = 1600;
	private boolean cont;
	private BlockingByteQueue speechBytes = new BlockingByteQueue();
	private BaseRecognizer recognizer;
	private EndpointerThread epThread;
	//public boolean generateEmptyResult = false;
	private RecognizerListeners listeners = new RecognizerListeners();
	private BlockingByteQueue audioData = new BlockingByteQueue();
	private AudioPort audioPort;
	private AudioFormat audioFormat;

	private boolean liveMode = true;

	public NuanceEndpointer() throws RecognizerException, InitializationException {
		this(new Microphone(8000, 1), true);
	}

	public NuanceEndpointer(AudioPort audioPort, boolean live) throws RecognizerException {
		this.audioPort = audioPort;
		this.audioFormat = audioPort.getAudioFormat();
		setLiveMode(live);
		audioPort.addAudioListener(this);
		try {
			recognizer = new BaseRecognizer();
		} catch (NuanceException e) {
			throw new RecognizerException(e.getMessage());
		}
		recognizer.setEpParameter("swiep_mode", "begin_end");
	}

	private class EndpointerThread extends Thread {
		@Override
		public void run() {
			try {
				boolean foundSpeech = endpoint(); 
				if (!foundSpeech) {
					NuanceResult result = new NuanceResult(RecResult.SILENCE);
					recognitionResult(result);
				}
			} catch (NuanceException e) {
				e.printStackTrace();
			}
		}
	}

	public void setLiveMode(boolean liveMode) {
		this.liveMode = liveMode;
	}
	/*
	private int power(double[] samples) {
		Double prevSample = null;
		double sumOfSquares = 0.0f;
		for (int i = 0; i < samples.length; i++) {
			if (prevSample != null) {
				double sample = samples[i] - prevSample;
				sumOfSquares += (sample * sample);
			}
			prevSample = samples[i];
		}
		double power = (10.0 * (Math.log10(sumOfSquares) - Math.log10(samples.length))) + 0.5;
		if (power < 0) power = 1.0;
		return (int) power;
	}
	*/

	private boolean endpoint() throws NuanceException {
		boolean result = false;
		if (!running) {
			if (liveMode)
				audioData.reset();

			running  = true;

			byte[] buffer = new byte[bufferSize];
			byte[] buffer16 = new byte[bufferSize * 2];

			IntByReference state = new IntByReference();
			IntByReference beginSample = new IntByReference();
			IntByReference endSample = new IntByReference();
			SWIepAudioSamples epSamples = new SWIepAudioSamples();
			epSamples.type = new WString(BaseRecognizer.getEncoding(audioFormat));
			epSamples.samples = new Memory(bufferSize);
			epSamples.len = bufferSize;
			boolean first = true;
			boolean speechStart = false;
			cont = true;
			speechBytes.reset();
			byte[] speechBuffer = new byte[bufferSize];
			int writePos = 0;
			listeners.initRecognition(audioFormat);

			recognizer.epStart();
			//recognizer.epPromptDone();

			// Collect new samples from audio source 
			while (cont) {
				int n = -1;
				try {
					if (audioFormat.getSampleRate() == 16000f) {
						n = audioData.read(buffer16, 0, buffer16.length);
						if (n > 0) {
							AudioUtil.resample(buffer16, audioFormat, buffer, 8000);
							speechBytes.write(buffer);
							writePos += buffer.length;
						}
					} else if (audioFormat.getSampleRate() == 8000f) {
						n = audioData.read(buffer, 0, buffer.length);
						if (n > 0) {
							speechBytes.write(buffer);	
							writePos += buffer.length;
						}
					} else {
						System.err.println("Bad sample rate : " + audioFormat.getSampleRate());
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (n == -1) {
					cont = false;
					if (speechStart) {
						listeners.endOfSpeech((writePos / 16000f));
						NuanceResult nresult = null;
						nresult = new NuanceResult(RecResult.FINAL, RecResult.NOMATCH);
						recognitionResult(nresult);
					}
					continue;
				}
				epSamples.samples.write(0, buffer, 0, bufferSize);

				if (first)
					epSamples.status = SWIrec.SWIrec_SAMPLE_FIRST;
				else
					epSamples.status = SWIrec.SWIrec_SAMPLE_CONTINUE;
				recognizer.epWrite(epSamples, state, beginSample, endSample);

				if (state.getValue() == SWIep.SWIep_IN_SPEECH) {
					if (!speechStart) {
						listeners.startOfSpeech((beginSample.getValue() / 8000f));
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
						speechSamples(speechBuffer, 0, speechBuffer.length);
					}
				} else if (state.getValue() == SWIep.SWIep_AFTER_SPEECH) {
					int rest = endSample.getValue() * 2 - (writePos - speechBytes.available());
					for (; rest >= speechBuffer.length; rest -= speechBuffer.length) {
						try {
							speechBytes.read(speechBuffer);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						speechSamples(speechBuffer, 0, speechBuffer.length);
					}
					listeners.endOfSpeech((endSample.getValue() / 8000f));
					NuanceResult nresult = null;
					//if (generateEmptyResult) {
					nresult = new NuanceResult(RecResult.FINAL, RecResult.NOMATCH);
					//}
					recognitionResult(nresult);

					result = true;
					break;
				} else if (state.getValue() == SWIep.SWIep_TIMEOUT) {
					break;
				} else if (state.getValue() == SWIep.SWIep_MAX_SPEECH) {
					listeners.endOfSpeech(writePos / 16000f);
					NuanceResult nresult = null;
					nresult = new NuanceResult(RecResult.MAXSPEECH, RecResult.NOMATCH);
					recognitionResult(nresult);
					result = true;
					break;
				} 
				first = false;
			}
			recognizer.epStop();

			running = false;
		}
		return result;
	}

	protected void recognitionResult(RecResult result) {
		listeners.recognitionResult(result);
	}

	protected void speechSamples(byte[] samples, int pos, int len) {
		listeners.speechSamples(samples, pos, len);
	}

	@Override
	public void startListen() throws RecognizerException {
		if (audioPort instanceof AudioGate)
			((AudioGate)audioPort).closeGate();
		epThread = new EndpointerThread();
		epThread.start();
	}

	@Override
	public boolean stopListen() throws RecognizerException {
		if (running) {
			try {
				cont = false;
				epThread.join();
				return true;
			} catch (InterruptedException e) {
				throw new RecognizerException(e.getMessage());
			}
		} else {
			return false;
		}
	}


	@Override
	public void setEndSilTimeout(int msec) {
		recognizer.setEpParameter("incompletetimeout", new Integer(msec).toString());
	}

	@Override
	public void setMaxSpeechTimeout(int msec) {
		recognizer.setEpParameter("maxspeechtimeout", new Integer(msec).toString());
	}

	@Override
	public void setNoSpeechTimeout(int msec) {
		recognizer.setEpParameter("timeout", new Integer(msec).toString());
	}

	@Override
	public void addRecognizerListener(RecognizerListener listener, int priority) {
		listeners.add(listener, priority);
	}

	@Override
	public AudioFormat getAudioFormat() {
		return audioPort.getAudioFormat();
	}

	@Override
	public void listenAudio(byte[] buffer, int pos, int len) {
		if (liveMode) {
			// Running in live mode, just discard audio if we are not listening
			if (running) {
				audioData.write(buffer, pos, len);
			}
		} else {
			// Running in off-line mode, wait until the endpointer runs and the buffer is empty
			//System.out.println(audioData.available());
			while (!running || audioData.available() > bufferSize * 2) {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			audioData.write(buffer, pos, len);
		}
	}

	@Override
	public void startListening() {
		audioData.reset();
	}

	@Override
	public void stopListening() {
		audioData.endWrite();
	}

	@Override
	public AudioPort getAudioPort() {
		return audioPort;
	}

}
