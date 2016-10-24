package iristk.speech;

import iristk.util.Record;
import iristk.util.Language;

public abstract class Context extends Record {
	
	@RecordField
	public String name;
	
	@RecordField
	public Language language;
	
	private String uniqueName = null; 
	
	private static int counter = 0;
	
	protected synchronized String getUniqueName() {
		if (uniqueName == null) {
			uniqueName = (name == null ? "context" : name) + "-" + counter++;
		}
		return uniqueName;	
	}
			
	public abstract void load(RecognizerModule recognizerModule) throws RecognizerException;

	public abstract void unload(RecognizerModule recognizerModule) throws RecognizerException;
	
	public abstract void activate(RecognizerModule recognizerModule) throws RecognizerException;
	
	public abstract void deactivate(RecognizerModule recognizerModule) throws RecognizerException;
	

}
