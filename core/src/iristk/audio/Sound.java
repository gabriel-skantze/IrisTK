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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Sound {

	private File file;
	private AudioFormat	audioFormat;
	private byte[] soundBytes;

	private Sound() {
	}
	
	public Sound(File file) throws UnsupportedAudioFileException, IOException {
		load(file);
	}
	
	public Sound(File file, float start, float length) throws UnsupportedAudioFileException, IOException {
		load(AudioSystem.getAudioInputStream(file), start, length);
	}
	
	public Sound(File file, float start, float length, int channel) throws UnsupportedAudioFileException, IOException {
		load(AudioSystem.getAudioInputStream(file), start, length, channel);
	}
	
	public Sound(URL url) throws UnsupportedAudioFileException, IOException {
		load(url);
	}
	
	public Sound(byte[] soundBytes, AudioFormat audioFormat) {
		this.soundBytes = soundBytes;
		this.audioFormat = audioFormat;
	}
	
	public Sound(AudioInputStream audio) throws IOException {
		load(audio);
	}
	
	@Override
	public Sound clone() {
		Sound s = new Sound();
		s.soundBytes = new byte[soundBytes.length];
		System.arraycopy(soundBytes, 0, s.soundBytes, 0, soundBytes.length);
		s.audioFormat = audioFormat;
		return s;
	}
	
	public void load(AudioInputStream audio, float start, float length) throws IOException {
		this.audioFormat = audio.getFormat();
		ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
		int	nBytesRead = 0;
		int bufferSize = 1024;
		byte[] buffer = new byte[bufferSize];
		int byteLength = AudioUtil.secondLengthToBytes(audioFormat, length);
		audio.skip(AudioUtil.secondLengthToBytes(audioFormat, start));
		while ((nBytesRead = audio.read(buffer, 0, bufferSize)) != -1) {
			if (dataStream.size() + nBytesRead > byteLength) {
				dataStream.write(buffer, 0, byteLength - dataStream.size());
				break;
			} else {
				dataStream.write(buffer, 0, nBytesRead);
			}
		}
		this.soundBytes = dataStream.toByteArray();
	}
	
	public void load(AudioInputStream audio, float start, float length, int channel) throws IOException {
		this.audioFormat = new AudioFormat(
					audio.getFormat().getEncoding(),
					audio.getFormat().getSampleRate(),
					audio.getFormat().getSampleSizeInBits(),
					1,
					audio.getFormat().getFrameSize() / audio.getFormat().getChannels(),
					audio.getFormat().getFrameRate(),
					audio.getFormat().isBigEndian());
		int nChannels = audio.getFormat().getChannels();
		ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
		int	nBytesRead = 0;
		int bufferSize = 1024;
		byte[] buffer = new byte[bufferSize];
		byte[] cbuffer = new byte[bufferSize / nChannels];
		int cByteLength = AudioUtil.secondLengthToBytes(audioFormat, length);
		audio.skip(AudioUtil.secondLengthToBytes(audio.getFormat(), start));
		while ((nBytesRead = audio.read(buffer, 0, bufferSize)) != -1) {
			int cBytesRead = nBytesRead / nChannels;
			for (int i = 0; i < cBytesRead; i += 2) {
				cbuffer[i] = buffer[i * nChannels + channel * 2];
				cbuffer[i+1] = buffer[i * nChannels + channel * 2 + 1];
			}
			if (length > -1 && dataStream.size() + cBytesRead > cByteLength) {
				dataStream.write(cbuffer, 0, cByteLength - dataStream.size());
				break;
			} else {
				dataStream.write(cbuffer, 0, cBytesRead);
			}
		}
		this.soundBytes = dataStream.toByteArray();
	}	
	
	public void load(AudioInputStream audio) throws IOException {
		this.audioFormat = audio.getFormat();
		ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
		int	nBytesRead = 0;
		int bufferSize = 1024;
		byte[] buffer = new byte[bufferSize];
		while ((nBytesRead = audio.read(buffer, 0, bufferSize)) != -1) {
			dataStream.write(buffer, 0, nBytesRead);
		}
		this.soundBytes = dataStream.toByteArray();
	}
	
	/*
	private void load(AudioInputStream audioInputStream, int bufferSize) throws UnsupportedAudioFileException, IOException {
		this.audioFormat = audioInputStream.getFormat();
		
		ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
		
		int	nBytesRead = 0;
		int pos = 0;
		byte[] buffer = new byte[bufferSize];
		while (true) {
			nBytesRead = audioInputStream.read(buffer, 0, bufferSize);
			if (nBytesRead == -1) 
				break;
			dataStream.write(buffer, 0, nBytesRead);
			//if (outStream != null) {
			//	outStream.write(buffer); 
			//}
			pos += nBytesRead;
		}
		this.soundBytes = dataStream.toByteArray();
		audioInputStream.close();	
	}
	*/
	
	public short getSample(int pos, int channel) {
		int p = pos * getAudioFormat().getFrameSize() + channel * 2;
		return AudioUtil.bytesToShort(getAudioFormat(), soundBytes[p], soundBytes[p + 1]);
	}
	
	public void setSample(int pos, int channel, short sample) {
		int p = pos * getAudioFormat().getFrameSize() + channel * 2;
		double[] db = new double[]{(double)sample / Short.MAX_VALUE};
		AudioUtil.doublesToBytes(getAudioFormat(), db, 0, 1, soundBytes, p);
	}
	
	public float getSecondsLength() {
		return getSampleLength() / audioFormat.getSampleRate();
	}
	
	public int getSampleLength() {
		return soundBytes.length / audioFormat.getFrameSize();
	}
	
	public void cropSeconds(float start, float end) {
		cropBytes(AudioUtil.secondLengthToBytes(audioFormat, start), AudioUtil.secondLengthToBytes(audioFormat, end));
	}
	
	public void cropSamples(int start, int end) {
		cropBytes(AudioUtil.sampleLengthToBytes(audioFormat, start), AudioUtil.sampleLengthToBytes(audioFormat, end));
	}
	
	public void cropBytes(int start, int end) {
		byte[] bytes = new byte[end - start];
		System.arraycopy(soundBytes, start, bytes, 0, bytes.length);
		this.soundBytes = bytes;
	}
	
	/*
	// Simultaneously load the sound and write the data to an OutputStream
	public void load(File file, OutputStream outStream, int bufferSize) throws UnsupportedAudioFileException, IOException {
		this.file = file;
		AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
		load(audioInputStream, outStream, bufferSize);
	}
	
	// Simultaneously load the sound and write the data to an OutputStream
	public void load(URL url, OutputStream outStream, int bufferSize) throws UnsupportedAudioFileException, IOException {
		AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(url);
		load(audioInputStream, outStream, bufferSize);	
	}
	 */
	
	public void load(File file) throws UnsupportedAudioFileException, IOException {
		load(AudioSystem.getAudioInputStream(file));	
	}
	
	public void load(URL url) throws UnsupportedAudioFileException, IOException {
		load(AudioSystem.getAudioInputStream(file));
	}

	public byte[] getBytes() {
		return soundBytes;
	}
	
	public void setBytes(byte[] data) {
		this.soundBytes = data;
	}
	
	public AudioFormat getAudioFormat() {
		return audioFormat;
	}
	
	public void setAudioFormat(AudioFormat format) {
		this.audioFormat = format;
	}

	public void save(File file) {
		this.file = file;
		ByteArrayInputStream bis = new ByteArrayInputStream(soundBytes);		
		AudioInputStream ais = new AudioInputStream(bis, audioFormat, soundBytes.length/audioFormat.getFrameSize());
		try {
			AudioSystem.write(ais, AudioFileFormat.Type.WAVE, file);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static SoundPlayer soundPlayer;
	
	public void play(int msecStart, int msecLength) {
		if (soundPlayer == null)
			soundPlayer = new SoundPlayer(getAudioFormat());
		soundPlayer.play(this, msecStart, msecLength);
	}
	
	public void play() {
		play(0, (int) (getSecondsLength() * 1000.0));
	}
	
	public void playAsync(int msecStart, int msecLength) {
		if (soundPlayer == null || !AudioUtil.equalFormats(soundPlayer.getAudioFormat(), getAudioFormat()))
			soundPlayer = new SoundPlayer(getAudioFormat());
		soundPlayer.playAsync(this, msecStart, msecLength);
	}
	
	public void playAsync() {
		playAsync(0, (int) (getSecondsLength() * 1000.0));
	}

	public Sound resample(int sampleRate) {
		byte[] buf = new byte[(((int) ((getBytes().length) * (sampleRate / getAudioFormat().getSampleRate()))) / 2) * 2];
		AudioUtil.resample(getBytes(), getAudioFormat(), buf, sampleRate);
		Sound s = new Sound(buf, AudioUtil.setSampleRate(getAudioFormat(), sampleRate));
		return s;
	}

	public Sound mix(Sound sound) {
		byte[] buf = new byte[soundBytes.length];
		Sound s = new Sound(buf, getAudioFormat());
		for (int i = 0; i < getSampleLength() && i < sound.getSampleLength(); i++) {
			s.setSample(i, 0, (short) ((getSample(i, 0) + sound.getSample(i, 0)) / 2));
		}
		return s;
	}
	
	public Sound mixStereo(Sound sound) {
		byte[] buf = new byte[soundBytes.length * 2];
		Arrays.fill(buf, (byte)0);
		Sound s = new Sound(buf, AudioUtil.setChannels(getAudioFormat(), 2));
		for (int i = 0; i < getSampleLength() && i < sound.getSampleLength(); i++) {
			s.setSample(i, 0, (short) getSample(i, 0));
			s.setSample(i, 1, (short) sound.getSample(i, 0));
		}
		return s;
	}

	public Sound amplify(double d) {
		byte[] buf = new byte[soundBytes.length];
		Sound s = new Sound(buf, getAudioFormat());
		for (int i = 0; i < getSampleLength(); i++) {
			s.setSample(i, 0, (short) ((getSample(i, 0) * d)));
		}
		return s;
	}
	
	
}
