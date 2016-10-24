package iristk.speech;

import iristk.audio.AudioListener;
import iristk.audio.AudioPort;
import iristk.audio.Microphone;
import iristk.system.InitializationException;
import iristk.util.BlockingByteQueue;

import javax.sound.sampled.AudioFormat;

/**
 * An Endpointer that uses an EnergyVAD to do the endpointing.
 */
public class EnergyEndpointer implements Endpointer, EnergyVADContainer {

	private AudioFormat audioFormat;
	private EnergyVAD vad;
	private boolean listening;
	private RecognizerListeners listeners = new RecognizerListeners();
	private int noSpeechTimeout = 8000;
	private int maxSpeechTimeout = 8000;
	private int endSilTimeout = 500;
	private ListeningThread listeningThread;
	private int bufferSize;
	private BlockingByteQueue speechData = new BlockingByteQueue();
	private AudioPort audioPort;

	private boolean inSpeech = false;
	private long endSilMsec = 0;
	private long startSilMsec = 0;
	private long inSpeechMsec = 0;
	private RecResult result = null;
	private boolean abort = false;
	
	/**
	 * An endpointer that operates on the default microphone (16Khz)
	 * @throws InitializationException
	 */
	public EnergyEndpointer() throws InitializationException {
		this(new Microphone());
	}

	public EnergyEndpointer(AudioPort audioPort) {
		this.audioPort = audioPort;
		this.audioFormat = audioPort.getAudioFormat();
		this.bufferSize = (int) (audioFormat.getSampleRate() * audioFormat.getSampleSizeInBits() / 800);
		this.vad = new EnergyVAD(audioPort.getDeviceName(), audioFormat);
		audioPort.addAudioListener(new AudioInput());
	}

	@Override
	public synchronized void startListen() throws RecognizerException {
		stopListen();
		speechData.reset();
		inSpeech = false;
		endSilMsec = 0;
		startSilMsec = 0;
		inSpeechMsec = 0;
		result = null;
		abort = false;
		listeningThread = new ListeningThread();
		listening = true;
	}

	@Override
	public synchronized boolean stopListen() throws RecognizerException {
		if (listening) {
			abort = true;
			try {
				listeningThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return true;
		} else {
			return false;
		}
	}

	private class ListeningThread extends Thread {

		private float timestamp = 0f;

		public ListeningThread() {
			start();
		}

		@Override
		public void run() {
			try {
				listeners.initRecognition(audioFormat);
				boolean started = false;
				byte[] readBuffer = new byte[bufferSize];
				boolean running = true;
				while (running) {
					int n = speechData.read(readBuffer, 0, bufferSize);
					if (n < bufferSize) {
						running = false;
					} else {
						if (!started) {
							started = true;
							listeners.startOfSpeech(timestamp - 0.2f);
						}
						listeners.speechSamples(readBuffer, 0, bufferSize);
						timestamp += 0.01;
					}
				}
				if (started) 
					listeners.endOfSpeech(timestamp - (endSilTimeout)/1000f);
				listeners.recognitionResult(result);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}


	@Override
	public void setNoSpeechTimeout(int msec) throws RecognizerException {
		this.noSpeechTimeout = msec;
	}

	@Override
	public void setEndSilTimeout(int msec) throws RecognizerException {
		this.endSilTimeout = msec;
	}

	@Override
	public void setMaxSpeechTimeout(int msec) throws RecognizerException {
		this.maxSpeechTimeout = msec;
	}

	@Override
	public AudioFormat getAudioFormat() {
		return audioFormat;
	}

	@Override
	public void addRecognizerListener(RecognizerListener listener, int priority) {
		listeners.add(listener, priority);
	}

	@Override
	public AudioPort getAudioPort() {
		return audioPort;
	}
	
	@Override
	public EnergyVAD getEnergyVAD() {
		return vad;
	}

	private class AudioInput implements AudioListener {
		
		private BlockingByteQueue vadBuffer = new BlockingByteQueue();
		// Keep 200ms lag for the VAD
		private int vadLag = 32 * 200;

		@Override
		public void listenAudio(byte[] buffer, int pos, int len) {
			//if (liveMode) {
			try {
				vad.processSamples(buffer, pos, len);
				// The vadBuffer keeps some speech samples to account for the lag in the start-of-speech detection
				vadBuffer.write(buffer, pos, len);
				if (vadBuffer.available() > vadLag) {
					vadBuffer.skip(len);
				}
				if (listening) {
					if (!inSpeech && vad.isInSpeech()) {
						// Start-of-speech
						byte[] readBuffer = new byte[len];
						// Fill with samples from the vadBuffer 
						while (vadBuffer.available() >= len) {
							vadBuffer.read(readBuffer);
							speechData.write(readBuffer, 0, len);
						}
						inSpeech = true;
					} 
					if (inSpeech) {
						speechData.write(buffer, pos, len);
						if (vad.isInSpeech()) {
							endSilMsec = 0;
							inSpeechMsec += len / 32;
							if (inSpeechMsec > maxSpeechTimeout) {
								// Too long utterance
								result = new RecResult(RecResult.MAXSPEECH);
								speechData.endWrite();
								listening = false;
							}
						} else {
							endSilMsec += len / 32;
							if (endSilMsec > endSilTimeout) {
								// End-of-speech
								result = new RecResult(RecResult.FINAL, RecResult.NOMATCH);
								speechData.endWrite();
								listening = false;
							}
						}
					} else {
						startSilMsec += len / 32;
						if (startSilMsec > noSpeechTimeout) {
							// No speech detected
							result = new RecResult(RecResult.SILENCE);
							speechData.endWrite();
							listening = false;
						}
					}
					if (abort) {
						// The listening was aborted before completed
						result = new RecResult(RecResult.FAILED);
						speechData.endWrite();
						listening = false;
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		@Override
		public void startListening() {
		}
	
		@Override
		public void stopListening() {
		}
		
	}


}
