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
	
	private static Logger logger = IrisUtils.getLogger(Microphone.class);
	
	private BlockingStream stream;
	private int channelCount;
	private AudioFormat audioFormat;
	private String deviceName;

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

	public Microphone(AudioFormat format) throws InitializationException {
		this(null, format);
	}

	public Microphone(String name, AudioFormat format) throws InitializationException {
		this.audioFormat = format;
		this.channelCount = format.getChannels();
		try {
			PortAudioUtil.initialize();
			int deviceId = PortAudio.getDefaultInputDevice();
			if (!isCompatible(deviceId, name, format)) {
				FIND_DEVICE: {
				for (int i = 0; i < PortAudio.getDeviceCount(); i++) {
					if (isCompatible(i, name, format)) {
						deviceId = i;
						break FIND_DEVICE;
					}
				}
				for (int i = 0; i < PortAudio.getDeviceCount(); i++) {
					if (isCompatible(i, null, format)) {
						deviceId = i;
						break FIND_DEVICE;
					}
				}
				throw new InitializationException("PortAudio: Could not find microphone with at least " + format.getChannels() + " channels");
			}
			}
			DeviceInfo deviceInfo = PortAudio.getDeviceInfo(deviceId);
			this.deviceName = deviceInfo.name;
			StreamParameters streamParams = new StreamParameters();
			streamParams.channelCount = channelCount;
			streamParams.device = deviceId;
			streamParams.sampleFormat = PortAudio.FORMAT_INT_16;
			//streamParams.suggestedLatency = deviceInfo.defaultLowInputLatency;
			int flags = 0;
			int framesPerBuffer = 256;
			stream = PortAudio.openStream(streamParams, null, (int)format.getSampleRate(), framesPerBuffer, flags);
			double latency = stream.getInfo().inputLatency;
			super.start();
			logger.info("PortAudio using " + deviceInfo.name + ", Latency: " + latency);
		} catch (Exception e) {
			throw new InitializationException(e.getMessage());
		}
	}

	
	private boolean isCompatible(int device, String name, AudioFormat format) {
		DeviceInfo deviceInfo = PortAudio.getDeviceInfo(device);
		return (deviceInfo.hostApi == PortAudio.HOST_API_TYPE_DEV && deviceInfo.maxInputChannels >= format.getChannels() &&
				(name == null || deviceInfo.name.contains(name)));
	}

	@Override
	protected void startSource() {
		stream.start();
	}

	@Override
	protected void stopSource() {
		stream.stop();
	}

	@Override
	public int readSource(byte[] buffer, int pos, int len) {
		short[] frames = new short[len/2];
		stream.read(frames, frames.length/channelCount);
		ByteBuffer bb = ByteBuffer.wrap(buffer);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		int slen = len / 2;
		for (int i = 0; i < slen; i++) {
			short val = frames[i];
			bb.putShort(pos + i * 2, val);
		}
		return len;
	}

	@Override
	public AudioFormat getAudioFormat() {
		return audioFormat;
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
	
	/*
	public Microphone(boolean b) {
		try {
			PortAudioUtil.initialize();
			channelCount = 2;
			audioFormat = new AudioFormat(Encoding.PCM_SIGNED, 16000, 16, channelCount, 2 * channelCount, 48000, false);
			int deviceId = 0;
			for (int i = 0; i < PortAudio.getDeviceCount(); i++) {
				
				if (PortAudio.getDeviceInfo(i).name.contains("Wireless")) {
					System.out.println(PortAudio.getDeviceInfo(i).name  + " " + PortAudio.getDeviceInfo(i).maxInputChannels + " " + PortAudio.getDeviceInfo(i).hostApi + " " + PortAudio.getDeviceInfo(i).defaultSampleRate);
					if (PortAudio.getDeviceInfo(i).hostApi == 2)
						deviceId = i;
					int m = PortAudio.HOST_API_TYPE_MME;
				}
			}
			DeviceInfo deviceInfo = PortAudio.getDeviceInfo(deviceId);
			StreamParameters streamParams = new StreamParameters();
			streamParams.channelCount = channelCount;
			streamParams.device = deviceId;
			streamParams.sampleFormat = PortAudio.FORMAT_INT_16;
			//streamParams.suggestedLatency = deviceInfo.defaultLowInputLatency;
			int flags = 0;
			int framesPerBuffer = 256;
			stream = PortAudio.openStream(streamParams, null, (int)audioFormat.getSampleRate(), framesPerBuffer, flags);
			double latency = stream.getInfo().inputLatency;
			super.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) throws Exception {
		AudioRecorder recorder = new AudioRecorder(new Microphone(true));
		recorder.startRecording(new File("test.wav"));
		System.out.println("Recording started");
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Recording stopped");
		recorder.stopRecording();
	}
	*/

}
