package iristk.speech;

import java.io.IOException;
import javax.xml.bind.JAXBException;

import org.xml.sax.SAXException;

import iristk.cfg.Grammar;
import iristk.cfg.Parser;
import iristk.cfg.SRGSGrammar;

public class SemanticGrammarContext extends Context {
	
	@RecordField
	public String grammar;
	
	private SemanticGrammarContext() {
	}
	
	public SemanticGrammarContext(Grammar grammar) {
		this(null, grammar);
	}
	
	public SemanticGrammarContext(String name, Grammar grammar) {
		SRGSGrammar g = new SRGSGrammar(grammar);
		this.name = name;
		this.language = g.getLanguage();
		this.grammar = g.toString();
	}
	
	public SemanticGrammarContext(String srgsString) throws JAXBException, SAXException, IOException {
		this(null, srgsString);
	}
	
	public SemanticGrammarContext(String name, String srgsString) throws JAXBException, SAXException, IOException {
		SRGSGrammar g = new SRGSGrammar(srgsString);
		this.name = name;
		this.language = g.getLanguage();
		this.grammar = g.toString();
	}
	
	private Parser getParser(RecognizerModule recognizerModule) {
		Parser parser = (Parser) recognizerModule.getSemanticProcessor(Parser.class.getName());
		if (parser == null) {
			parser = new Parser();
			recognizerModule.addSemanticProcessor(Parser.class.getName(), parser);
		}
		return parser;
	}

	@Override
	public void load(RecognizerModule recognizerModule) throws RecognizerException {
		try {
			getParser(recognizerModule).loadGrammar(getUniqueName(), new SRGSGrammar(grammar));
		} catch (JAXBException e) {
			throw new RecognizerException(e.getMessage());
		} catch (SAXException e) {
			throw new RecognizerException(e.getMessage());
		} catch (IOException e) {
			throw new RecognizerException(e.getMessage());
		}
	}

	@Override
	public void unload(RecognizerModule recognizerModule) throws RecognizerException {
		getParser(recognizerModule).unloadGrammar(getUniqueName());
	}

	@Override
	public void activate(RecognizerModule recognizerModule) throws RecognizerException {
		getParser(recognizerModule).activateGrammar(getUniqueName());
	}

	@Override
	public void deactivate(RecognizerModule recognizerModule) throws RecognizerException {
		getParser(recognizerModule).deactivateGrammar(getUniqueName());
	}
}
