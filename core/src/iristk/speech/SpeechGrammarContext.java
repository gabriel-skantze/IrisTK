package iristk.speech;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.bind.JAXBException;

import org.xml.sax.SAXException;

import iristk.cfg.Grammar;
import iristk.cfg.SRGSGrammar;
import iristk.util.Language;

public class SpeechGrammarContext extends Context {

	@RecordField
	public String grammar;
	
	@RecordField
	public String uri;
	
	private SpeechGrammarContext() {
	}
	
	public SpeechGrammarContext(Grammar grammar) {
		SRGSGrammar g = new SRGSGrammar(grammar);
		this.language = g.getLanguage();
		this.grammar = g.toString();
	}
	
	public SpeechGrammarContext(URI uri, Language lang) {
		this.uri = uri.toString();
		this.language = lang;
	}
	
	public SpeechGrammarContext(String srgsString) throws SAXException, IOException, JAXBException {
		SRGSGrammar g = new SRGSGrammar(srgsString);
		this.language = g.getLanguage();
		this.grammar = g.toString();
	}
	
	@Override
	public void load(RecognizerModule recognizerModule) throws RecognizerException {
		Recognizer recognizer = recognizerModule.getRecognizer();
		if (recognizer instanceof GrammarRecognizer) {
			GrammarRecognizer gRec = (GrammarRecognizer)recognizer;
			//TODO: check that grammar is supported (using factory method)
			if (uri != null) {
				try {
					gRec.loadGrammar(getUniqueName(), language, new URI(uri));
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
			} else if (grammar != null) {
				gRec.loadGrammar(getUniqueName(), language, grammar);
			}
		} else {
			throw new RecognizerException("Speech Grammar not supported");
		}
	}

	@Override
	public void unload(RecognizerModule recognizerModule) throws RecognizerException {
		Recognizer recognizer = recognizerModule.getRecognizer();
		if (recognizer instanceof GrammarRecognizer) {
			GrammarRecognizer gRec = (GrammarRecognizer)recognizer;
			gRec.unloadGrammar(getUniqueName());
		}
	}

	@Override
	public void activate(RecognizerModule recognizerModule) throws RecognizerException {
		Recognizer recognizer = recognizerModule.getRecognizer();
		if (recognizer instanceof GrammarRecognizer) {
			recognizer.activateContext(getUniqueName(), 1f);
		}
	}

	@Override
	public void deactivate(RecognizerModule recognizerModule) throws RecognizerException {
		Recognizer recognizer = recognizerModule.getRecognizer();
		if (recognizer instanceof GrammarRecognizer) {
			recognizer.deactivateContext(getUniqueName());
		}
	}

	
}
