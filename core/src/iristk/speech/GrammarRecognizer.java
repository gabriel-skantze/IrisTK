package iristk.speech;

import iristk.util.Language;

import java.net.URI;

public interface GrammarRecognizer {

	void loadGrammar(String contextName, Language language, String srgs) throws RecognizerException;
	
	void loadGrammar(String contextName, Language language, URI uri) throws RecognizerException;
	
	void unloadGrammar(String contextName) throws RecognizerException;
	
}
