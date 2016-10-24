package iristk.system;

import java.io.File;

import iristk.audio.AudioPort;
import iristk.audio.AudioRecorder;

public class RecordingModule extends IrisModule {

	private AudioRecorder audioRecorder;
	private String name;
	private boolean recording = false;

	public RecordingModule(AudioPort audioPort, String name) {
		audioRecorder = new AudioRecorder(audioPort);
		this.name = name;
	}
	
	@Override
	public void onEvent(Event event) {
		if (event.triggers("action.record.start") && name.equals(event.getString("name"))) {
			if (!recording ) {
				recording = true;
				audioRecorder.startRecording(new File(event.getString("file")));
			}
		} else if (event.triggers("action.record.stop") && name.equals(event.getString("name"))) {
			if (recording) {
				recording = false;
				audioRecorder.stopRecording();
			}
		} 
	}

	@Override
	public void init() throws InitializationException {
	}


}
