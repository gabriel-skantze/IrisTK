package iristk.speech;

public interface OpenVocabularyRecognizer {

	void loadOpenVocabulary(String contextName, OpenVocabularyContext context) throws RecognizerException;
	
	void unloadOpenVocabulary(String contextName) throws RecognizerException;
}
