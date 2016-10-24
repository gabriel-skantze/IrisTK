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
package iristk.speech.prosody;

import iristk.audio.AudioListener;
import iristk.audio.AudioUtil;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.sound.sampled.AudioFormat;

public class ProsodyTracker implements AudioListener {

	private final double threshold = 0.15;
	private final double globalThreshold = 0.5;
	private final double energyThreshold = 40;
	private final int frameRate = 100;

	private int minTau;
	private int maxTau;
	
	private final int bufferSize;
	private final int bufferStepSize;
	private final int overlapSize;
	private final double sampleRate;
	
	private long samplesProcessed = 0;
	
	/**
	 * The original input buffer
	 */
	private final double[] inputBuffer;

	/**
	 * The buffer that stores the calculated values.
	 * It is exactly half the size of the input buffer.
	 */
	private double[] yinBuffer;
	//private PitchSmoother pitchSmoother;
	private List<ProsodyListener> listeners = new ArrayList<>();
	//private PitchNormalizer pitchNormalizer = new PitchNormalizer();

	private AudioFormat audioFormat;
	
	private double[] inputSamples = null;
	private final int sampleSize;
	
	public ProsodyTracker(AudioFormat audioFormat) {
		this(audioFormat, 50, 500);
	}
	
	public ProsodyTracker(AudioFormat audioFormat, double minPitchHz, double maxPitchHz) {
		this.audioFormat = audioFormat;
		this.sampleRate = audioFormat.getSampleRate();
		this.sampleSize = audioFormat.getSampleSizeInBits() / 8;
		minTau = (int) (sampleRate / maxPitchHz);
		maxTau = (int) (sampleRate / minPitchHz);
		bufferStepSize = (int) (sampleRate / frameRate);
		bufferSize = Math.max(maxTau * 2, bufferStepSize);
		overlapSize = (bufferSize - bufferStepSize); 
		inputBuffer = new double[bufferSize];
		reset();
	}
	
	public ProsodyTracker(AudioFormat audioFormat, ProsodyNormalizer normalizer) {
		this(audioFormat, normalizer.getPitchHzLowerBound(), normalizer.getPitchHzUpperBound());
	}

	public void addProsodyListener(ProsodyListener listener) {
		listeners.add(listener);
	}
		
	private double energy() {
        double sumOfSquares = 0.0f;
        for (int i = 0; i < inputBuffer.length; i++) {
            double sample = inputBuffer[i];
            sumOfSquares += sample * sample;
        }
        double power = (10.0 * (Math.log10(sumOfSquares) - Math.log10(inputBuffer.length))) + 0.5;
		if (power < 0) power = 1.0;
		return power;
        //return  Math.sqrt(sumOfSquares/inputBuffer.length);
    }
	
	/**
	 * Implements the difference function as described
	 * in step 2 of the YIN paper
	 */
	private void difference(){
		int j,tau;
		double delta;
		for(tau=0;tau < yinBuffer.length;tau++){
			yinBuffer[tau] = 0;
		}
		for(tau = 1 ; tau < yinBuffer.length ; tau++){
			for(j = 0 ; j < yinBuffer.length ; j++){
				delta = inputBuffer[j] - inputBuffer[j+tau];
				yinBuffer[tau] += delta * delta;
			}
		}
	}
	

	/**
	 * The cumulative mean normalized difference function
	 * as described in step 3 of the YIN paper
	 * <br><code>
	 * yinBuffer[0] == yinBuffer[1] = 1
	 * </code>
	 *
	 */
	private void cumulativeMeanNormalizedDifference(){
		int tau;
		yinBuffer[0] = 1;
		//Very small optimization in comparison with AUBIO
		//start the running sum with the correct value:
		//the first value of the yinBuffer
		double runningSum = yinBuffer[1];
		//yinBuffer[1] is always 1
		yinBuffer[1] = 1;
		//now start at tau = 2
		for(tau = 2 ; tau < yinBuffer.length ; tau++){
			runningSum += yinBuffer[tau];
			yinBuffer[tau] *= tau / runningSum;
		}
	}

	/**
	 * Implements step 4 of the YIN paper
	 */
	private Integer absoluteThreshold(double[] yinBuffer, int min, int max){
		//Uses another loop construct
		//than the AUBIO implementation
		for(int tau = min;tau<max;tau++){
			if(yinBuffer[tau] < threshold){
				while(tau+1 < yinBuffer.length &&
						yinBuffer[tau+1] < yinBuffer[tau])
					tau++;
				return tau;
			}
		}
		double bestConf = 1.0f;
		Integer best = -1;
		for(int tau = min; tau < max; tau++){
			if (yinBuffer[tau] < bestConf && yinBuffer[tau] < globalThreshold) {
				best = tau;
				bestConf = yinBuffer[tau];
			}
		}
		return best;
	}

	/**
	 * Implements step 5 of the YIN paper. It refines the estimated tau value
	 * using parabolic interpolation. This is needed to detect higher
	 * frequencies more precisely.
	 *
	 * @param tauEstimate
	 *            the estimated tau value.
	 * @return a better, more precise tau value.
	 */
	private double parabolicInterpolation(int tauEstimate, double[] yinBuffer) {
		double s0, s1, s2;
		int x0 = (tauEstimate < 1) ? tauEstimate : tauEstimate - 1;
		int x2 = (tauEstimate + 1 < yinBuffer.length) ? tauEstimate + 1 : tauEstimate;
		if (x0 == tauEstimate)
			return (yinBuffer[tauEstimate] <= yinBuffer[x2]) ? tauEstimate : x2;
		if (x2 == tauEstimate)
			return (yinBuffer[tauEstimate] <= yinBuffer[x0]) ? tauEstimate : x0;
		s0 = yinBuffer[x0];
		s1 = yinBuffer[tauEstimate];
		s2 = yinBuffer[x2];
		//fixed AUBIO implementation, thanks to Karl Helgason:
		//(2.0f * s1 - s2 - s0) was incorrectly multiplied with -1
		return tauEstimate + 0.5f * (s2 - s0 ) / (2.0f * s1 - s2 - s0);
	}

	/**
	 * The main flow of the YIN algorithm. Returns a pitch value in Hz or null if
	 * no pitch is detected using the current values of the input buffer.
	 *
	 * @return a pitch value in Hz or null if no pitch is detected.
	 */
	private double getPitch(double energy) {

		if (energy < energyThreshold) {
			return -1;
		}
		
		int tauEstimate = -1;
		double pitchInHertz = -1;

		//step 2
		difference();

		//step 3
		cumulativeMeanNormalizedDifference();

		//step 4
		tauEstimate = absoluteThreshold(yinBuffer, minTau, maxTau);

		//step 5
		if(tauEstimate != -1){
			 double betterTau = parabolicInterpolation(tauEstimate, yinBuffer);

			//step 6
			//TODO Implement optimization for the YIN algorithm.
			//0.77% => 0.5% error rate,
			//using the data of the YIN paper
			//bestLocalEstimate()

			//conversion to Hz
			pitchInHertz = sampleRate/betterTau;
		}

		return pitchInHertz;
	}

	public double getConf(int tau) {
		return yinBuffer[tau];
	}
	
	@Override
	public void listenAudio(byte[] samples, int pos, int length) {
		if (inputSamples == null || inputSamples.length != length / sampleSize) {
			inputSamples = new double[length / sampleSize];
		}
		AudioUtil.bytesToDoubles(audioFormat, samples, pos, length, inputSamples, 0);
		processSamples(inputSamples);
	}

	@Override
	public void startListening() {
	}

	@Override
	public void stopListening() {
	}

	public void processSamples(double[] samples) {
		processSamples(samples, 0, samples.length);
	}

	// Samples are assumed to be from -1.0 to 1.0
	public void processSamples(double[] samples, int pos, int length) {	
		int samplesPos = 0;
		
		while (samplesPos < length) {
			int samplesLeft = length - samplesPos;
			int samplesToFill = bufferStepSize;
			if (samplesProcessed % bufferStepSize != 0) {
				samplesToFill = (int) (bufferStepSize - (samplesProcessed % bufferStepSize));
			} 
			if (samplesToFill > samplesLeft) {
				samplesToFill = samplesLeft;
			}
			System.arraycopy(inputBuffer, samplesToFill, inputBuffer, 0, bufferSize - samplesToFill);
			System.arraycopy(samples, pos + samplesPos, inputBuffer, bufferSize - samplesToFill, samplesToFill);
			AudioUtil.scaleDoubles(inputBuffer, bufferSize - samplesToFill, samplesToFill, 32768);
			samplesPos += samplesToFill;
			samplesProcessed += samplesToFill;
			
			if (samplesProcessed % bufferStepSize == 0) {
				
				yinBuffer = new double[maxTau];
				double energy = energy();
				double pitch = getPitch(energy);
				double conf = -1;
				if (pitch > -1) {
					conf = getConf((int) (sampleRate / pitch));
				}
		
				double pitchCent = -1;
				if (pitch != -1) 
					pitchCent = AudioUtil.pitchHzToCent(pitch);
				
				ProsodyData rawData = new ProsodyData(pitchCent, energy, conf, (samplesProcessed - 480) / sampleRate);
				
				//ProsodyData smoothedData = pitchSmoother.process(rawData);
				
				for (ProsodyListener listener : listeners) {
					listener.prosodyData(rawData);
				}
			}
		}
	}

	public void reset() {
		//pitchSmoother = new PitchSmoother();
		samplesProcessed = 0;
		Arrays.fill(inputBuffer, 0.0f);
	}

	public static List<ProsodyData> clone(List<ProsodyData> list) {
		ArrayList<ProsodyData> clone = new ArrayList<>();
		for (ProsodyData pd : list) {
			clone.add(new ProsodyData(pd));
		}
		return clone;
	}
	
	public static List<ProsodyData> filter(List<ProsodyData> list, double energyThreshold) {
		List<ProsodyData> result = new ArrayList<>();
		List<ProsodyData> cluster = new ArrayList<>();
		double lp = Double.MAX_VALUE;
		int gap = 0;
		for (int i = 0; i < list.size(); i++) {
			ProsodyData pd = list.get(i);
			if (pd.pitch != -1) {
				if (gap <= 1 && (Math.abs(lp - pd.pitch) < 200)) {
				} else {
					addCluster(result, cluster, energyThreshold);
					cluster = new ArrayList<>();
				}
				lp = pd.pitch;
				gap = 0;
			} else {
				gap++;
			}
			cluster.add(new ProsodyData(pd));
		}
		addCluster(result, cluster, energyThreshold);
		return result;
	}
	
	private static void addCluster(List<ProsodyData> result, List<ProsodyData> cluster, double energyThreshold) {
		int voiced = 0;
		double maxEnergy = Double.MIN_VALUE;
		for (ProsodyData pd : cluster) {
			if (pd.pitch != -1) {
				voiced++;
				maxEnergy = Math.max(maxEnergy, pd.energy);
			}
		}
		//System.out.println(voiced + " " +maxEnergy);
		if (voiced < 6 || maxEnergy < energyThreshold) {
			for (ProsodyData pd : cluster) {
				pd.pitch = -1;
				pd.conf = -1;
			}
		}
		result.addAll(cluster);
	}


	/*
	public static void main(String[] args) throws Exception {
		List<ProsodyData> data = analyze(new File("D:/TM/Audio/1416217490744.mic-red.wav"), null);
		dumpHz(data, new File("D:/TM/Test/1416217490744.mic-red.f0"));
		dumpHz(filter(data, 60), new File("D:/TM/Test/1416217490744.mic-red.f0f"));
	}

	public static List<ProsodyData> analyze(File wavFile, ProsodyNormData nd) throws UnsupportedAudioFileException, IOException {
		final List<ProsodyData> result = new ArrayList<>();
		FileAudioSource source = new FileAudioSource(wavFile);
		ProsodyTracker tracker;
		if (nd != null)
			tracker = new ProsodyTracker(source.getAudioFormat(), nd);
		else
			tracker = new ProsodyTracker(source.getAudioFormat());
		source.addAudioListener(tracker);
		tracker.addProsodyListener(new ProsodyListener() {
			@Override
			public void prosodyData(ProsodyData data) {
				result.add(data);
			}
		});
		source.start();
		source.waitFor();
		return result;
	}
	*/
	
	public static void dumpHz(List<ProsodyData> data, File outFile) throws FileNotFoundException {
		PrintStream out = new PrintStream(outFile);
		for (ProsodyData pd : data) {
			if (pd.pitch != -1)
				out.println(AudioUtil.pitchCentToHz(pd.pitch));
			else
				out.println(-10);
		}
		out.close();
	}

	
}
