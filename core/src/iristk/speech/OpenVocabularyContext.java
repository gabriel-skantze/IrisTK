package iristk.speech;

import java.util.List;

import iristk.util.Language;

public class OpenVocabularyContext extends Context {
	
	@RecordField
	public List phrases;
	
	private OpenVocabularyContext() {
	}
	
	public OpenVocabularyContext(Language lang) {
		this.language = lang;
	}
	
	public OpenVocabularyContext(Language lang, List phrases) {
		this.language = lang;
		this.phrases = phrases;
	}

	public OpenVocabularyContext(String name, Language lang, List phrases) {
		this.name = name;
		this.language = lang;
		this.phrases = phrases;
	}	
	
	@Override
	public void load(RecognizerModule recognizerModule) throws RecognizerException {
		Recognizer recognizer = recognizerModule.getRecognizer();
		if (recognizer instanceof OpenVocabularyRecognizer) {
			OpenVocabularyRecognizer oRec = (OpenVocabularyRecognizer)recognizer;
			oRec.loadOpenVocabulary(getUniqueName(), this);
		} else {
			throw new RecognizerException("Open Vocabulary not supported");
		}
	}

	@Override
	public void unload(RecognizerModule recognizerModule) throws RecognizerException {
		Recognizer recognizer = recognizerModule.getRecognizer();
		if (recognizer instanceof OpenVocabularyRecognizer) {
			OpenVocabularyRecognizer oRec = (OpenVocabularyRecognizer)recognizer;
			oRec.unloadOpenVocabulary(getUniqueName());
		}
	}

	@Override
	public void activate(RecognizerModule recognizerModule) throws RecognizerException {
		Recognizer recognizer = recognizerModule.getRecognizer();
		if (recognizer instanceof OpenVocabularyRecognizer) {
			recognizer.activateContext(getUniqueName(), 1f);
		}
	}

	@Override
	public void deactivate(RecognizerModule recognizerModule) throws RecognizerException {
		Recognizer recognizer = recognizerModule.getRecognizer();
		if (recognizer instanceof OpenVocabularyRecognizer) {
			recognizer.deactivateContext(getUniqueName());
		}
	}

}
