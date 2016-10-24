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

import iristk.system.IrisUtils;

import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;

import org.slf4j.Logger;
import com.portaudio.BlockingStream;
import com.portaudio.DeviceInfo;
import com.portaudio.PortAudio;
import com.portaudio.StreamParameters;

public class Speaker extends AudioTarget {
	
	public static final double SUGGESTED_LATENCY = 0.1;

	private static Logger logger = IrisUtils.getLogger(Speaker.class);

	private AudioFormat format;
	private BlockingStream stream;
	private String deviceName;

	public Speaker(String deviceName, AudioFormat format) {
		this.format = format;
		try {
			PortAudioUtil.initialize();
		} catch (Exception e) {
			e.printStackTrace();
		}
		//System.out.println(format);
		int deviceId = PortAudio.getDefaultOutputDevice();
		if (deviceName != null) {
			for (int i = 0; i < PortAudio.getDeviceCount(); i++) {
				DeviceInfo info = PortAudio.getDeviceInfo(i);
				if (info.maxOutputChannels > 0 && info.hostApi == 0 && info.name.toUpperCase().contains(deviceName.toUpperCase())) {
					deviceId = i;
					break;
				}
			}
		}
		
		DeviceInfo deviceInfo = PortAudio.getDeviceInfo( deviceId );
		this.deviceName = deviceInfo.name;
		StreamParameters streamParams = new StreamParameters();
		streamParams.channelCount = format.getChannels();
		streamParams.device = deviceId;
		streamParams.sampleFormat = PortAudio.FORMAT_INT_16;
		streamParams.suggestedLatency = SUGGESTED_LATENCY;
		int flags = 0;
		int framesPerBuffer = 256;
		stream = PortAudio.openStream(null, streamParams, (int)format.getSampleRate(), framesPerBuffer, flags);
		double latency = stream.getInfo().outputLatency;
		logger.info("PortAudio using " + deviceInfo.name + ", Latency: " + latency);
	}
	
	public Speaker(AudioFormat format) {
		this(null, format);
	}

	public Speaker(int sampleRate, int channelCount) {
		this(null, sampleRate, channelCount);
	}
	
	public Speaker(String deviceName, int sampleRate, int channelCount) {
		this(deviceName, new AudioFormat(Encoding.PCM_SIGNED, sampleRate, 16, channelCount, 2, sampleRate, false));
	}
	
	public Speaker() {
		this(null, 16000, 1);
	}
	
	public Speaker(String deviceName) {
		this(deviceName, 16000, 1);
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
			if (info.maxOutputChannels > 0 && info.hostApi == 0) {
				result.add(info.name);
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
		return PortAudio.getDeviceInfo(PortAudio.getDefaultOutputDevice()).name; 
	}
	
	public static boolean hasDevice(String deviceName) {
		try {
			PortAudioUtil.initialize();
		} catch (Exception e) {
			e.printStackTrace();
		}
		for (int i = 0; i < PortAudio.getDeviceCount(); i++) {
			DeviceInfo info = PortAudio.getDeviceInfo(i);
			if (info.maxOutputChannels > 0 && info.hostApi == 0 && info.name.toUpperCase().contains(deviceName.toUpperCase())) {
				return true;
			}
		}
		return false;
	}
	
	public static void main(String[] args) {
		Speaker.getDevices();
	}
	
	@Override
	public void startTarget() {
		stream.start();
	}

	@Override
	public void stopTarget() {
		stream.stop();
	}

	@Override
	public AudioFormat getAudioFormat() {
		return format;
	}

	@Override
	public void writeTarget(byte[] buffer, int pos, int len) {
		int sampleSize = getAudioFormat().getSampleSizeInBits() / 8; 
		short[] sbuffer = new short[len / 2];
		for (int i = 0; i < sbuffer.length; i++) {
			int bufPos = pos + i * sampleSize;
	        sbuffer[i] = AudioUtil.bytesToShort(format, buffer[bufPos], buffer[bufPos + 1]);
		}
		stream.write(sbuffer, sbuffer.length / format.getChannels());
	}
	
	@Override
	public String getDeviceName() {
		return deviceName;
	}

	//@Override
	//public void closeTarget() {
	//	stream.close();
	//}

}
