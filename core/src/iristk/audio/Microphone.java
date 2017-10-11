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

import iristk.system.InitializationException;
import iristk.system.IrisUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;

import org.mozilla.javascript.edu.emory.mathcs.backport.java.util.Arrays;
import org.slf4j.Logger;

import com.portaudio.BlockingStream;
import com.portaudio.DeviceInfo;
import com.portaudio.PortAudio;
import com.portaudio.StreamParameters;

/**
 * Microphone is an AudioSource that actively reads audio from the sound card. 
 * To access this audio, one or more AudioListeners must be attached. 
 */
public class Microphone extends AudioSource {

	public static double SUGGESTED_LATENCY = 0.1;
	
	private static Logger logger = IrisUtils.getLogger(Microphone.class);

	private BlockingStream stream;
	private int channelCount;
	private AudioFormat outAudioFormat;
	private AudioFormat inAudioFormat;
	private String deviceName;
	private int channelSelect;
	private int portAudioCount;

	private boolean running = false;

	public Microphone() throws InitializationException {
		this(null, 16000, 1);
	}

	public Microphone(String name) throws InitializationException {
		this(name, 16000, 1);
	}

	public Microphone(int sampleRate, int channelCount) throws InitializationException {
		this(null, sampleRate, channelCount);
	}

	public Microphone(String name, int sampleRate, int channelCount) throws InitializationException {
		this(name, new AudioFormat(Encoding.PCM_SIGNED, sampleRate, 16, channelCount, 2 * channelCount, sampleRate, false));
	}

	public Microphone(String name, int sampleRate, int channelCount, int channelSelect) throws InitializationException {
		this(name, new AudioFormat(Encoding.PCM_SIGNED, sampleRate, 16, channelCount, 2 * channelCount, sampleRate, false), channelSelect);
	}

	public Microphone(AudioFormat format) throws InitializationException {
		this(null, format);
	}

	public Microphone(String name, AudioFormat format) throws InitializationException {
		this(name, format, -1);
	}

	public Microphone(String name, AudioFormat format, int channelSelect) throws InitializationException {
		this.inAudioFormat = format;
		if (channelSelect != -1)
			this.outAudioFormat = AudioUtil.setChannels(format, 1);
		else
			this.outAudioFormat = format;
		this.channelCount = format.getChannels();
		this.channelSelect = channelSelect;
		this.deviceName = name;
		try {
			PortAudioUtil.initialize();
			setupStream();
			super.start();
		} catch (Exception e) {
			throw new InitializationException(e.getMessage());
		}
	}
	
	private void setupStream() throws InitializationException {
		if (stream != null) {
			try {
				stream.stop();
				stream.close();
			} catch (Exception e) {
			}
		}
		try {
			this.stream = null;
			if (getDevices().size() == 0) {
				throw new InitializationException("No microphone found");
			}
			int deviceId = PortAudio.getDefaultInputDevice();
			if (!isCompatible(deviceId, deviceName, inAudioFormat)) {
				FIND_DEVICE: {
				for (int i = 0; i < PortAudio.getDeviceCount(); i++) {
					if (isCompatible(i, deviceName, inAudioFormat)) {
						deviceId = i;
						break FIND_DEVICE;
					}
				}
				logger.warn("No device with the name '" + deviceName + "' found");
				for (int i = 0; i < PortAudio.getDeviceCount(); i++) {
					if (isCompatible(i, null, inAudioFormat)) {
						deviceId = i;
						break FIND_DEVICE;
					}
				}
				throw new InitializationException("Could not find microphone with at least " + inAudioFormat.getChannels() + " channels");
			}
			}
			portAudioCount = PortAudioUtil.getStartCount();
			DeviceInfo deviceInfo = PortAudio.getDeviceInfo(deviceId);
			this.deviceName = deviceInfo.name;
			StreamParameters streamParams = new StreamParameters();
			streamParams.channelCount = channelCount;
			streamParams.device = deviceId;
			streamParams.sampleFormat = PortAudio.FORMAT_INT_16;
			streamParams.suggestedLatency = SUGGESTED_LATENCY ;
			int flags = 0;
			int framesPerBuffer = 256;
			stream = PortAudio.openStream(streamParams, null, (int)inAudioFormat.getSampleRate(), framesPerBuffer, flags);
			if (running)
				stream.start();
			String info = "Opening " + deviceInfo.name;
			if (channelSelect != -1)
				info += ", channel " + channelSelect;
			info += ", Latency: " + (int)(stream.getInfo().inputLatency * 1000d) + "ms";
			logger.info(info);
		} catch (Exception e) {
			this.stream = null;
			throw new InitializationException(e.getMessage());
		}
	}

	public synchronized void setDevice(String name, int channelCount, int channelSelect) throws InitializationException {
		if (deviceName.equals(name) && channelCount == this.channelCount && channelSelect == this.channelSelect)
			return;
		int channelCountOut = channelSelect != -1 ? 1 : channelCount;
		if (outAudioFormat.getChannels() != channelCountOut)
			throw new InitializationException("Output audio format changed when switching microphone device");
		this.inAudioFormat = AudioUtil.setChannels(inAudioFormat, channelCount);
		this.channelCount = channelCount;
		this.channelSelect = channelSelect;
		this.deviceName = name;
		setupStream();
	}

	private boolean isCompatible(int device, String name, AudioFormat format) {
		DeviceInfo deviceInfo = PortAudio.getDeviceInfo(device);
		return (deviceInfo.hostApi == PortAudio.HOST_API_TYPE_DEV && deviceInfo.maxInputChannels >= format.getChannels() &&
				(name == null || deviceInfo.name.toUpperCase().contains(name.toUpperCase())));
	}

	@Override
	protected void startSource() {
		running  = true;
		stream.start();
	}

	@Override
	protected void stopSource() {
		try {
			stream.stop();
		} catch (Exception e) {
		}
		running = false;
	}

	@Override
	public synchronized int readSource(byte[] buffer, int pos, int len) {
		if (stream == null) {
			//System.out.println("stream is null");
			try {
				if (portAudioCount == PortAudioUtil.getStartCount()) {
					PortAudioUtil.restart();
				}

				//System.out.println("setup stream 2");
				setupStream();
			} catch (InitializationException e) {
				logger.error("Microphone lost");
				return -1;
			}
		}
		int fact = channelSelect != -1 ? channelCount : 1;

		try {
			short[] frames = new short[(fact*len)/2];

			stream.read(frames, frames.length/channelCount);

			ByteBuffer bb = ByteBuffer.wrap(buffer);
			bb.order(ByteOrder.LITTLE_ENDIAN);
			int slen = len / 2;
			for (int i = 0; i < slen; i++) {
				int fp = channelSelect == -1 ? i : i * channelCount + channelSelect;
				short val = frames[fp];
				bb.putShort(pos + i * 2, val);
			}

		} catch (Exception e) {
			//System.out.println(e.getMessage());
			//System.out.println("Setting stream to null");
			Arrays.fill(buffer, (byte)0);
			stream = null;
		}
		return len;
	}

	@Override
	public AudioFormat getAudioFormat() {
		return outAudioFormat;
	}

	public static DeviceInfo getDeviceInfo(String deviceName) {
		try {
			PortAudioUtil.initialize();
		} catch (Exception e) {
			e.printStackTrace();
		}
		for (int i = 0; i < PortAudio.getDeviceCount(); i++) {
			DeviceInfo info = PortAudio.getDeviceInfo(i);
			if (info.maxInputChannels > 0 && info.hostApi == 0 && info.name.toUpperCase().contains(deviceName.toUpperCase())) {
				return info;
			}
		}
		return null;
	}

	public static List<String> getDevices() {
		try {
			PortAudioUtil.initialize();
		} catch (Exception e) {
			e.printStackTrace();
		}
		List<String> result = new ArrayList<>();
		for (int i = 0; i < PortAudio.getDeviceCount(); i++) {
			DeviceInfo info = PortAudio.getDeviceInfo(i);
			if (info.maxInputChannels > 0 && info.hostApi == 0) {
				result.add(info.name.trim());
			}
		}
		return result;
	}

	public static String getDefaultDevice() {
		try {
			PortAudioUtil.initialize();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return PortAudio.getDeviceInfo(PortAudio.getDefaultInputDevice()).name; 
	}

	public static boolean hasDevice(String deviceName) {
		try {
			PortAudioUtil.initialize();
		} catch (Exception e) {
			e.printStackTrace();
		}
		for (int i = 0; i < PortAudio.getDeviceCount(); i++) {
			DeviceInfo info = PortAudio.getDeviceInfo(i);
			if (info.maxInputChannels > 0 && info.hostApi == 0 && info.name.toUpperCase().contains(deviceName.toUpperCase())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String getDeviceName() {
		return deviceName;
	}

	public static void main(String[] args) throws Exception {
		System.out.println(getDevices());
		/*
		Microphone mic = new Microphone(null, 16000, 1, 0);
		ProsodyTracker pro = new ProsodyTracker(mic.getAudioFormat());
		mic.addAudioListener(pro);
		pro.addProsodyListener(new ProsodyListener() {
			@Override
			public void prosodyData(ProsodyData pd) {
				//System.out.println(pd.energy +  " "  + pd.conf);
			}
		});
		*/
	}

}
