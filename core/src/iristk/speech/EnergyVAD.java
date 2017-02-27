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

import iristk.audio.AudioPort;
import iristk.audio.AudioUtil;

import java.util.ArrayList;
import java.util.Arrays;
import javax.sound.sampled.AudioFormat;

import iristk.util.Histogram;
import iristk.util.MedianFilter;
import iristk.util.Parameter;

public class EnergyVAD implements VAD {

	// The window size (in frames), which smoothes the result, but gives a lag
	public static final int WINSIZE = 21;

	private static final int SPEECH = 1;
	private static final int SILENCE = 0;

	public static final int DEFAULT_SPEECH_LEVEL = 35;
	public static final int DEFAULT_SILENCE_LEVEL = 20;
	
	private int[] stateWindow = new int[WINSIZE];
	{
		Arrays.fill(stateWindow, SILENCE);
	}
	private int stateWindowPos = 0;

	private int[] zcrWindow = new int[WINSIZE];
	{
		Arrays.fill(zcrWindow, 0);
	}
	
	private double prevSample = 0.0;

	private boolean calibrating = false;

	private long streamPos = 0;

	private Histogram silenceHistogram = new Histogram(100, 3000); 
	private Histogram speechHistogram = new Histogram(100, 300); 

	private ArrayList<EnergyVAD.Listener> vadListeners = new ArrayList<>();

	private int state = SILENCE;
	private final AudioFormat audioFormat;
	private double[] inputSamples = null;
	private double[] frameBuffer = new double[2048];
	private int frameBufferPos = 0;
	private final int sampleSize;
	private double[] frame;
	private String deviceName;
	//private Parameters parameters;
	
	private int silenceLevel = DEFAULT_SILENCE_LEVEL;
	
	//public Parameter<Boolean> adaptSilenceLevel = new Parameter<>(true);
	public Parameter<Boolean> adaptSpeechLevel = new Parameter<>(false);
	//public Parameter<Integer> silenceLevel = new Parameter<>(20);
	public Parameter<Integer> speechLevel = new Parameter<>(DEFAULT_SPEECH_LEVEL);
	
	public EnergyVAD(AudioFormat audioFormat) {
		this(null, audioFormat);
	}
	
	public EnergyVAD(AudioPort audioPort) {
		this(audioPort.getDeviceName(), audioPort.getAudioFormat());
		audioPort.addAudioListener(this);
	}
	
	public EnergyVAD(String deviceName, AudioFormat audioFormat) {
		this.deviceName = deviceName;
		this.audioFormat = audioFormat;
		this.sampleSize = audioFormat.getSampleSizeInBits() / 8;
		//this.parameters = new Parameters();
		frame = new double[(int) (audioFormat.getSampleRate() / 100)];
		/*
		if (deviceName != null) {
			File file = getStoreFile();
			if (file.exists()) {
				try {
					parameters.read(file);
					System.out.println("Reading VAD parameters from " + file.getAbsolutePath());
					System.out.println(parameters);
					return;
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		*/
	}
	
	//public static void setStoreDirectory(File directory) {
	//	storeDirectory = directory;
	//}
	
	public String getDeviceName() {
		return deviceName;
	}
	
	/*
	public void storeParameters() {
		if (deviceName != null) {
			storeDirectory.mkdirs();
			try {
				File file = getStoreFile();
				parameters.store(file);
				System.out.println("VAD parameters stored to " + file.getAbsolutePath());
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			System.err.println("Cannot store vad parameters without a device name");
		}
	}
	
	private File getStoreFile() {
		return new File(storeDirectory, "EnergyVAD/" + URLEncoder.encode(deviceName) + ".properties");
	}
	
	
	public void setParameters(Parameters parameters) {
		this.parameters = parameters;
	}
	
	public Parameters getParameters() {
		return parameters;
	}
	
	*/

	@Override
	public void addVADListener(EnergyVAD.Listener vadListener) {
		this.vadListeners.add(vadListener);
	}

	private int power(double[] samples) {
		double sumOfSquares = 0.0f;
		for (int i = 0; i < samples.length; i++) {
			double sample = samples[i] - prevSample;
			sumOfSquares += (sample * sample);
			prevSample = samples[i];
		}
		double power = (10.0 * (Math.log10(sumOfSquares) - Math.log10(samples.length))) + 0.5;
		if (power < 0) power = 1.0;
		return (int) power;
	}
	
	private int power2(double[] samples) {
		double sumOfSquares = 0.0f;
		for (int i = 0; i < samples.length; i++) {
			sumOfSquares += Math.pow(samples[i] / Short.MAX_VALUE, 2);
		}
		double power = 10.0 * Math.log10(Math.sqrt(sumOfSquares/samples.length)) + 50;
		if (power < 0) power = 0.0;
		if (power > 99) power = 99;
		return (int)Math.round(power);
	}
	
	// Returns a value between 0 and 100
	private int power3(double[] samples) {
		double sumOfSquares = 0.0f;
		for (int i = 0; i < samples.length; i++) {
			sumOfSquares += Math.pow(samples[i] / Short.MAX_VALUE, 2);
		}
		if (sumOfSquares == 0)
			return 0;
		double power = 10.0 * Math.log(sumOfSquares/samples.length) + 100;
		if (power < 0) power = 0.0;
		return (int)Math.round(power);
	}
	
	public static void main(String[] args) {
		System.out.println(Math.log10(160));
	}
	
	public synchronized void resetAdaptation() {
		speechHistogram.reset();
		silenceHistogram.reset();
	}

	public void processSamples(double[] samples, boolean scale) {
		System.arraycopy(samples, 0, frameBuffer, frameBufferPos, samples.length);
		frameBufferPos += samples.length;
		while (frameBufferPos >= frame.length) {    	
			System.arraycopy(frameBuffer, 0, frame, 0, frame.length);
			System.arraycopy(frameBuffer, frame.length, frameBuffer, 0, frameBufferPos - frame.length);
			frameBufferPos -= frame.length;
			if (scale)
				AudioUtil.scaleDoubles(frame, Short.MAX_VALUE);
			processFrame();
		}
	}

	private double smoothValue = 0;
	private double smoothLevel = 50;
	
	private void smoothFrame(double[] frame) {
		for (int i = 0; i < frame.length; i++) {
		    smoothValue += (frame[i] - smoothValue) / smoothLevel;
		    frame[i] = smoothValue;
		}
	}

	public void processSamples(byte[] samples, int pos, int length) {
		if (inputSamples  == null || inputSamples.length != length / sampleSize) {
			inputSamples = new double[length / sampleSize];
		}
		AudioUtil.bytesToDoubles(audioFormat, samples, pos, length, inputSamples, 0);
		processSamples(inputSamples, true);
	}

	
	private void processFrame() {
		streamPos += frame.length;

		//smoothFrame(frame);

		/*
		AudioUtil.scaleDoubles(frame, 1d / Short.MAX_VALUE);
		byte[] target = new byte[frame.length * 2];
		AudioUtil.doublesToBytes(audioFormat, frame, 0, frame.length, target, 0);
		try {
			Test.file.write(target, 0, target.length);
		} catch (IOException e) {
			e.printStackTrace();
		}
		AudioUtil.scaleDoubles(frame, Short.MAX_VALUE);
		*/

		int power = power2(frame);
		
		//System.out.println(silenceLevel + " " + power + " " + speechLevel.get());
		
		int tempSpeechLevel = speechLevel.get();
		if (tempSpeechLevel < silenceLevel + 5)
			tempSpeechLevel = silenceLevel + 5;
		
		int newState;
		if (state == SPEECH) {
			//parameters.silenceLevel + parameters.deltaSil
			if (power <= ((tempSpeechLevel - silenceLevel) / 2 + silenceLevel)) {
				newState = SILENCE;
			} else {
				newState = SPEECH;
			}
		} else {
			//parameters.silenceLevel + parameters.deltaSpeech
			if (power > (tempSpeechLevel)) {
				newState = SPEECH;
			} else {
				newState = SILENCE;
			}			   
		}

		silenceHistogram.add(power);
		if (silenceHistogram.size() > 100) {
			// Adapt silence level
			int ind = silenceHistogram.getMax();
			silenceLevel = ind;
			double silenceFluct = Math.max(silenceHistogram.getNegStdDev(silenceLevel) * 2, 3);
			//if (speechLevel.get() < silenceLevel + 5)
			//	speechLevel.set(silenceLevel + 5);
			
			if (adaptSpeechLevel.get() && power > silenceLevel + silenceFluct) {
				speechHistogram.add(power);
				int level = speechHistogram.getPercentile(0.25);
				speechLevel.set(level);
			}
		}
			
		stateWindow[stateWindowPos] = newState;
		stateWindowPos++;
		if (stateWindowPos >= WINSIZE)
			stateWindowPos = 0;

		Histogram stateHist = new Histogram(2);
		stateHist.addAll(stateWindow);
		state = stateHist.getMax();

		powerMedianFilter.add(power);
		
		for (EnergyVAD.Listener listener : vadListeners) {
			listener.vadEvent(streamPos, state == SPEECH, powerMedianFilter.getMedian());
		}
		
	}
	
	private MedianFilter powerMedianFilter = new MedianFilter(WINSIZE);

	private int zcr(double[] frame) {
		int c = 0;
		for (int i = 0; i < frame.length-1; i++) {
			if ((frame[i] < 0 && frame[i+1] > 0) || (frame[i] > 0 && frame[i+1] < 0))
				c++;
		}
		return c;
	}

	public boolean isInSpeech() {
		//return (!calibrating && (state == SPEECH || silFrames < endSil));
		return (!calibrating && (state == SPEECH));
	}

	public void reset() {
		Arrays.fill(stateWindow, SILENCE);
		state = SILENCE;
		stateWindowPos = 0;
		streamPos = 0;
	}

	
	@Override
	public void listenAudio(byte[] buffer, int pos, int len) {
		processSamples(buffer, pos, len);
	}

	@Override
	public void startListening() {
	}

	@Override
	public void stopListening() {
	}
	
	/*
	public static class Parameters extends Record {

		@RecordField
		public Integer silenceLevel = 20;
		@RecordField
		public Integer deltaSpeech = 6;
		@RecordField
		public Integer deltaSil = 3;
		@RecordField
		public Integer speechLevel = 35;
		
		public void read(File file) throws IOException {
			putAll(Record.fromProperties(file));
		}
		
		public void store(File file) throws IOException {
			toProperties(file);
		}
		
	}
	*/
	
	public int getSilenceLevel() {
		return silenceLevel;
	}

	public int getSpeechLevel() {
		return speechLevel.get();
	}
	
}
