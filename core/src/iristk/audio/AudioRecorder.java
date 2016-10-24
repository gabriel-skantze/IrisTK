package iristk.audio;

import iristk.kinect.KinectAudioSource;
import iristk.kinect.KinectV2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioFormat.Encoding;


/**
 * AudioRecorder is an AudioListener that records the audio to a file. 
 */
public class AudioRecorder {

	private FileOutputStream audioStream = null;
	private long startTime;
	private double writePos;
	private int frameSize;
	private byte[] silence;
	private AudioFormat audioFormat;
	private File tmpFile;
	private AudioPort audioPort;

	public AudioRecorder(AudioPort audioPort) {
		this.audioPort = audioPort;
		audioPort.addAudioListener(new RecorderListener());
		this.audioFormat = audioPort.getAudioFormat();
		frameSize = audioFormat.getFrameSize() * 100;
		silence = new byte[frameSize];
		Arrays.fill(silence, (byte)0);
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				stopRecording();
			}
		});
	}

	private class RecorderListener implements AudioListener {

		@Override
		public void listenAudio(byte[] buffer, int pos, int len) {
			synchronized (AudioRecorder.this) {
				//System.out.println(audioPort.getDeviceName() + " " + len);
				if (audioStream != null) {
					try {
						audioStream.write(buffer, pos, len);
						
						/*
						short[] tmp = new short[len/2];
						AudioUtil.bytesToShorts(audioFormat, buffer, 0, len, tmp, 0);
						for (int i = 0; i < tmp.length; i++) {
							audioStream.write(("" + tmp[i] + "\n").getBytes());
						}
						*/
						
						audioStream.flush();
						writePos += AudioUtil.byteLengthToSeconds(audioFormat, len);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}

		@Override
		public void startListening() {
			synchronized (AudioRecorder.this) {
				if (audioStream != null) {
					double timePos = (System.currentTimeMillis() - startTime) / 1000.0;
					while (timePos > writePos) {
						try {
							audioStream.write(silence, 0, silence.length);
							writePos += AudioUtil.byteLengthToSeconds(audioFormat, silence.length);
						} catch (IOException e) {
							e.printStackTrace();
						}
						
					}
				}
			}
		}

		@Override
		public synchronized void stopListening() {
		}

	}
	
	public synchronized void startRecording(File file) {
		tmpFile = new File(file.getAbsolutePath() + "." + (int)audioFormat.getSampleRate() + "." + audioFormat.getChannels() + ".tmp");
		try {
			audioStream = new FileOutputStream(tmpFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		startTime = System.currentTimeMillis();
		writePos = 0;
	}

	public synchronized void stopRecording() {
		if (audioStream != null) {
			try {
				audioStream.close();
				audioStream = null;
				makeWavFile(tmpFile);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static void makeWavFile(File tmpFile) throws IOException {
		Pattern p = Pattern.compile("(.*)\\.(\\d+)\\.(\\d+)\\.tmp");
		Matcher m = p.matcher(tmpFile.getAbsolutePath());
		if (m.matches()) {
			String recordingFile = m.group(1);
			if (!recordingFile.endsWith(".wav")) {
				recordingFile = recordingFile + ".wav";
			}
			int sampleRate = Integer.parseInt(m.group(2));
			int channelCount = Integer.parseInt(m.group(3));
			FileInputStream fi = new FileInputStream(tmpFile);
			AudioFormat audioFormat = new AudioFormat(Encoding.PCM_SIGNED, sampleRate, 16, channelCount, 2 * channelCount, sampleRate, false);
			AudioInputStream ai = new AudioInputStream(fi, audioFormat, tmpFile.length() / 2);
			AudioSystem.write(ai, AudioFileFormat.Type.WAVE, new File(recordingFile));
			fi.close();
			tmpFile.delete();
		}
	}
	
	public static void fixUncompletedLogs(String[] args) {
		if (args.length == 0)
			fixUncompletedLogs(new File(System.getProperty("user.dir")));
		else
			fixUncompletedLogs(new File(args[0]));
	}
	
	public static void fixUncompletedLogs(File dir) {
		if (dir.isDirectory() && dir.exists()) {
			for (File file : dir.listFiles()) {
				if (file.getAbsolutePath().endsWith(".tmp")) {
					try {
						makeWavFile(file);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		} else {
			System.err.println(dir + " is not a directory");
		}
	}
	
	public static void main(String[] args) throws Exception {
		AudioRecorder recorder = new AudioRecorder(new Microphone("Realtek", 16000, 2));
		recorder.startRecording(new File("Realtek.wav"));
		AudioRecorder recorder2 = new AudioRecorder(new Microphone("Wireless", 16000, 2));
		recorder2.startRecording(new File("Wireless.wav"));
		KinectV2 kinect = new KinectV2();
		kinect.setManualBeamAngle(0);
		KinectAudioSource kinectAudio = new KinectAudioSource(kinect);
		AudioRecorder recorder3 = new AudioRecorder(kinectAudio);
		recorder3.startRecording(new File("Kinect.wav"));
		System.out.println("Recording started");
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Recording stopped");
		recorder.stopRecording();
		recorder2.stopRecording();
		recorder3.stopRecording();
		System.exit(0);
	}

	public Object getAudioPort() {
		return audioPort;
	}

	public boolean isRecording() {
		return audioStream != null;
	}

}
