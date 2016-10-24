package iristk.speech.wit;

import iristk.speech.Context;
import iristk.speech.RecognizerException;
import iristk.speech.RecognizerModule;

public class WitContext extends Context {

	private String key;

	public WitContext(String key) {
		this.key = key;
	}
	
	private WitListener getWitListener(RecognizerModule recognizerModule) {
		if (recognizerModule.getSemanticProcessor("wit") == null) {
			recognizerModule.addSemanticProcessor("wit", new WitListener());
		}
		return (WitListener) recognizerModule.getSemanticProcessor("wit");
	}
	
	@Override
	public void load(RecognizerModule recognizerModule) throws RecognizerException {
	}

	@Override
	public void unload(RecognizerModule recognizerModule) throws RecognizerException {
	}

	@Override
	public void activate(RecognizerModule recognizerModule) throws RecognizerException {
		getWitListener(recognizerModule).activate(key);
	}

	@Override
	public void deactivate(RecognizerModule recognizerModule) throws RecognizerException {
		getWitListener(recognizerModule).deactivate(key);
	}

}
