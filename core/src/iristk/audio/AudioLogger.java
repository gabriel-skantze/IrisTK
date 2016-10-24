package iristk.audio;

import java.io.File;
import java.io.IOException;

import iristk.system.Logger;

public class AudioLogger implements Logger {

	private String name;
	private Boolean enabled;
	private AudioRecorder audioRecorder;

	public AudioLogger(String name, AudioPort audioPort, Boolean enabled) {
		this.name = name;
		this.audioRecorder = new AudioRecorder(audioPort);
		setEnabled(enabled);
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public void startLogging(File dir) throws IOException {
		if (enabled) {
			audioRecorder.startRecording(new File(dir, name + ".wav"));
		}
	}

	@Override
	public void stopLogging() throws IOException {
		if (audioRecorder.isRecording()) {
			audioRecorder.stopRecording();
		}
	}

}
