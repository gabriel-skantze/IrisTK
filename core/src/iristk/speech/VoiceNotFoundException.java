package iristk.speech;

import iristk.system.InitializationException;

public class VoiceNotFoundException extends InitializationException {

	public VoiceNotFoundException(String message) {
		super(message);
	}
	
	public VoiceNotFoundException(Voice voice) {
		super("Voice '" + voice.getName() + "' not found");
	}

}
