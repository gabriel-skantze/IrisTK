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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;

public class AudioUtil {

	/**
	 * Converts doubles (from -1.0 to 1.0) to bytes according to format
	 */
	public static void doublesToBytes(AudioFormat format, double[] source, int sourcePos, int sourceLen, byte[] target, int targetPos) {
		if (format.getSampleSizeInBits() == 16) {
			ByteBuffer bb = ByteBuffer.wrap(target);
			if (format.isBigEndian())
				bb.order(ByteOrder.BIG_ENDIAN);
			else
				bb.order(ByteOrder.LITTLE_ENDIAN);
			for (int i = 0; i < sourceLen; i++) {
				double val = source[i + sourcePos];
				short sval = (short) (val * Short.MAX_VALUE);
				bb.putShort(targetPos + i * 2, sval);
			}
		} else if (format.getSampleSizeInBits() == 8) {
			for (int i = 0; i < sourceLen; i++) {
				double val = source[i + sourcePos];
				byte bval = (byte) (val * Byte.MAX_VALUE);
				target[targetPos + i] = bval;
			}
		}
	}

	/**
	 * Converts floats (from -1.0 to 1.0) to bytes according to format
	 */
	public static void floatsToBytes(AudioFormat format, float[] source, int sourcePos, int sourceLen, byte[] target, int targetPos) {
		if (format.getSampleSizeInBits() == 16) {
			ByteBuffer bb = ByteBuffer.wrap(target);
			if (format.isBigEndian())
				bb.order(ByteOrder.BIG_ENDIAN);
			else
				bb.order(ByteOrder.LITTLE_ENDIAN);
			for (int i = 0; i < sourceLen; i++) {
				double val = source[i + sourcePos];
				short sval = (short) (val * Short.MAX_VALUE);
				bb.putShort(targetPos + i * 2, sval);
			}
		} else if (format.getSampleSizeInBits() == 8) {
			for (int i = 0; i < sourceLen; i++) {
				double val = source[i + sourcePos];
				byte bval = (byte) (val * Byte.MAX_VALUE);
				target[targetPos + i] = bval;
			}
		}
	}

	/**
	 * Converts bytes to doubles (from -1.0 to 1.0) according to format
	 */
	public static void bytesToDoubles(AudioFormat format, byte[] source, int sourcePos, int sourceLen, double[] target, int targetPos) {
		if (format.getSampleSizeInBits() == 16) {
			ByteBuffer bb = ByteBuffer.wrap(source);
			if (format.isBigEndian())
				bb.order(ByteOrder.BIG_ENDIAN);
			else
				bb.order(ByteOrder.LITTLE_ENDIAN);
			for (int i = 0; i < sourceLen / 2; i++) {
				short val = bb.getShort(i * 2 + sourcePos);
				double dval = ((double)val) / (double)Short.MAX_VALUE;
				target[i + targetPos] = dval;
			}
		} else if (format.getSampleSizeInBits() == 8) {
			for (int i = 0; i < sourceLen; i++) {
				double dval = ((double)source[i + sourcePos]) / (double)Byte.MAX_VALUE;
				target[i + targetPos] = dval;
			}
		}
	}

	public static void bytesToShorts(AudioFormat format, byte[] source, int sourcePos, int sourceLen, short[] target, int targetPos) {
		if (format.getSampleSizeInBits() == 16) {
			ByteBuffer bb = ByteBuffer.wrap(source);
			if (format.isBigEndian())
				bb.order(ByteOrder.BIG_ENDIAN);
			else
				bb.order(ByteOrder.LITTLE_ENDIAN);
			for (int i = 0; i < sourceLen / 2; i++) {
				target[i + targetPos] = bb.getShort(i * 2 + sourcePos);
			}
		} else if (format.getSampleSizeInBits() == 8) {
			for (int i = 0; i < sourceLen; i++) {
				target[i + targetPos] = (short) (source[i + sourcePos] * 256);
			}
		}
	}
	

	public static void shortsToBytes(AudioFormat format, short[] source, int sourcePos, int sourceLen, byte[] target, int targetPos) {
		if (format.getSampleSizeInBits() == 16) {
			ByteBuffer bb = ByteBuffer.wrap(target);
			if (format.isBigEndian())
				bb.order(ByteOrder.BIG_ENDIAN);
			else
				bb.order(ByteOrder.LITTLE_ENDIAN);
			for (int i = 0; i < sourceLen; i++) {
				bb.putShort(i*2, source[i]);
			}
		}
	}


	public static void bytesToIntegers(AudioFormat format, byte[] source, int sourcePos, int sourceLen, int[] target, int targetPos) {
		if (format.getSampleSizeInBits() == 16) {
			ByteBuffer bb = ByteBuffer.wrap(source);
			if (format.isBigEndian())
				bb.order(ByteOrder.BIG_ENDIAN);
			else
				bb.order(ByteOrder.LITTLE_ENDIAN);
			for (int i = 0; i < sourceLen / 2; i++) {
				target[i + targetPos] = bb.getShort(i * 2 + sourcePos);
			}
		} else if (format.getSampleSizeInBits() == 8) {
			for (int i = 0; i < sourceLen; i++) {
				target[i + targetPos] = source[i + sourcePos] * 256;
			}
		}
	}
	
	public static float byteLengthToSeconds(AudioFormat format, int len) {
		return len / (format.getSampleRate() * format.getFrameSize());
	}
	
	public static int byteLengthToSamples(AudioFormat format, int len) {
		return len / format.getFrameSize();
	}
	
	public static float sampleLengthToSeconds(AudioFormat format, int len) {
		return len / format.getSampleRate();
	}
	
	public static int sampleLengthToBytes(AudioFormat format, int len) {
		return len * format.getFrameSize();
	}

	public static int secondLengthToBytes(AudioFormat format, double len) {
		return (int)(len * format.getSampleRate()) * format.getFrameSize();
	}

	public static int secondLengthToSamples(AudioFormat format, double len) {
		return (int) (len * (format.getSampleRate()));
	}
	
	public static short bytesToShort(AudioFormat format, byte b1, byte b2) {
		byte big;
		byte little;
		if (!format.isBigEndian()) {
			little = b1;
			big = b2;
		} else {
			little = b2;
			big = b1;
		}
		int val = big;
		if (format.getEncoding() == AudioFormat.Encoding.PCM_UNSIGNED) {
			val &= 0xff; 
		}
		return (short) ((val << 8) + (little & 0xff));
	}

	public static void scaleDoubles(double[] doubles, double scaleFactor) {
		for (int i = 0; i < doubles.length; i++) {
			doubles[i] = doubles[i] * scaleFactor;
		}
	}

	public static void scaleDoubles(double[] doubles, int pos, int len, double scaleFactor) {
		for (int i = 0; i < len; i++) {
			doubles[i + pos] = doubles[i + pos] * scaleFactor;
		}
	}

	// Assuming mono sound, 2 bytes per frame (16 bit)
	public static void resample(byte[] fromBuffer, AudioFormat format, byte[] toBuffer, int toSampleRate) {
		resample(fromBuffer, 0, fromBuffer.length, format, toBuffer, toSampleRate);		
	}

	public static void resample(byte[] fromBuffer, int fromPos, int fromLen, AudioFormat format, byte[] toBuffer, int toSampleRate) {
		ByteBuffer bb = ByteBuffer.wrap(toBuffer);
		if (format.isBigEndian())
			bb.order(ByteOrder.BIG_ENDIAN);
		else
			bb.order(ByteOrder.LITTLE_ENDIAN);
		double[] dbuf = new double[fromBuffer.length/2];
		bytesToDoubles(format, fromBuffer, fromPos, fromLen, dbuf, 0);
		float resampleRatio = format.getSampleRate() / toSampleRate;
		int toLen = (int) (fromLen / resampleRatio) / 2;
		int resamplePos;
		double sample;
		for (int i = 0; i < toLen; i += 1) {
			resamplePos = (int) (i * resampleRatio);
			sample = dbuf[resamplePos];
			if (resamplePos == 0) {
				sample = (sample*2 + dbuf[resamplePos+1]) / 3;
			} else if (resamplePos >= dbuf.length-1) {
				sample = (dbuf[resamplePos-1] + sample*2) / 3; 
			} else {
				sample = (dbuf[resamplePos-1] + sample*2 + dbuf[resamplePos+1]) / 4;
			}
			bb.putShort(i*2, (short) (sample * Short.MAX_VALUE));
		}
	}
	
	public static int power(double[] samples, int pos, int length) {
		double sumOfSquares = 0.0f;
		Double prev = null;
		int len = 0;
		for (int i = 0; i < length; i++) {
			if (i + pos >= samples.length)
				break;
			if (prev != null) {
				double sample = (samples[i + pos] - prev) * Short.MAX_VALUE;
				sumOfSquares += (sample * sample);
				len++;
			}
			prev = samples[i + pos];
		}
		double power = (10.0 * (Math.log10(sumOfSquares) - Math.log10(len))) + 0.5;
		if (power < 0) power = 1.0;
		return (int) power;
	}
	
	private static final double CENT_CONST = 1731.2340490667560888319096172f;
	
    public static double pitchCentToHz(double pitchCent) {
    	return (Math.exp(pitchCent / CENT_CONST) * 110);
	}
    
    public static double pitchHzToCent(double pitchHz) {
    	return (CENT_CONST * Math.log(pitchHz / 110));
	}

	public static boolean equalFormats(AudioFormat format1, AudioFormat format2) {
		return (format1.getEncoding() == format2.getEncoding() &&
				format1.getChannels() == format2.getChannels() &&
				format1.getSampleRate() == format2.getSampleRate());
	}

	public static AudioFormat setChannels(AudioFormat format, int channels) {
		return new AudioFormat(
				format.getEncoding(),
				format.getSampleRate(),
				format.getSampleSizeInBits(),
				channels,
				channels * (format.getFrameSize() / format.getChannels()),
				format.getFrameRate(),
				format.isBigEndian());
	}
	
	public static AudioFormat setSampleRate(AudioFormat format, int sampleRate) {
		return new AudioFormat(
				format.getEncoding(),
				sampleRate,
				format.getSampleSizeInBits(),
				format.getChannels(),
				format.getFrameSize(),
				sampleRate,
				format.isBigEndian());
	}
	
	/**
	 * 
	 * @param sampleRate
	 * @param channelCount
	 * @return An AudioFormat with signed little-endian 16-bit encoding
	 */
	public static AudioFormat getAudioFormat(int sampleRate, int channelCount) {
		return new AudioFormat(Encoding.PCM_SIGNED, sampleRate, 16, channelCount, 2 * channelCount, sampleRate, false);
	}


}
