package iristk.speech;

import java.io.File;

import javax.sound.sampled.AudioFormat;

public interface SynthesizerEngine {

	/**
	 * Synthesizes text and writes the audio to a wav-file
	 * @param text The text to synthesize
	 * @param file The wav-file to write
	 * @return The transcription of the synthesized text
	 */
	public abstract Transcription synthesize(String text, File file);
	
	/**
	 * Transcribes a text
	 * @param text The text to synthesize
	 * @return The transcription of the text if it would be synthesized
	 */
	public abstract Transcription transcribe(String text);

	/**
	 * @return the audio format of the wav-files that are produced by this synthesizer
	 */
	public abstract AudioFormat getAudioFormat();

	public abstract Voice getVoice();
}
