package iristk.speech;

import iristk.audio.AudioPort;
import iristk.audio.Sound;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.UnsupportedAudioFileException;

public abstract class EndpointerRecognizer implements Recognizer {

	private Endpointer endpointer;

	public EndpointerRecognizer(Endpointer endpointer) {
		this.endpointer = endpointer;
	}
	
	public Endpointer getEndpointer() {
		return endpointer;
	}
	
	@Override
	public void startListen() throws RecognizerException {
		endpointer.startListen();
	}

	@Override
	public boolean stopListen() throws RecognizerException {
		return endpointer.stopListen();
	}

	@Override
	public void setNoSpeechTimeout(int msec) throws RecognizerException {
		endpointer.setNoSpeechTimeout(msec);
	}

	@Override
	public void setEndSilTimeout(int msec) throws RecognizerException {
		endpointer.setEndSilTimeout(msec);
	}

	@Override
	public void setMaxSpeechTimeout(int msec) throws RecognizerException {
		endpointer.setMaxSpeechTimeout(msec);
	}

	@Override
	public void addRecognizerListener(RecognizerListener listener, int priority) {
		endpointer.addRecognizerListener(listener, priority);
	}

	@Override
	public AudioFormat getAudioFormat() {
		return endpointer.getAudioFormat();
	}

	public static RecResult recognizeFile(File file, RecognizerListener recListener) throws RecognizerException {
		try {
			return recognizeSound(new Sound(file), recListener);
		} catch (UnsupportedAudioFileException e) {
			throw new RecognizerException(e.getMessage());
		} catch (IOException e) {
			throw new RecognizerException(e.getMessage());
		}
	}
	
	public static RecResult recognizeSound(Sound sound, RecognizerListener recListener) throws RecognizerException {
		RecResult result = new RecResult(RecResult.FINAL);
		recListener.initRecognition(sound.getAudioFormat());
		recListener.startOfSpeech(0);
		recListener.speechSamples(sound.getBytes(), 0, sound.getBytes().length);
		recListener.endOfSpeech(sound.getSecondsLength());
		recListener.recognitionResult(result);
		return result;
	}
	
	@Override
	public void setPartialResults(boolean cond) {
	}

	@Override
	public void setNbestLength(int length) {
	}

	@Override
	public RecResult recognizeFile(File file) throws RecognizerException {
		return null;
	}

	@Override
	public AudioPort getAudioPort() {
		return endpointer.getAudioPort();
	}


}
