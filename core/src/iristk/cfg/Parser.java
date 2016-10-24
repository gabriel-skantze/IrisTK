/*******************************************************************************
 * Copyright (c) 2014 Gabriel Skantze.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Gabriel Skantze - initial API and implementation
 ******************************************************************************/
package iristk.cfg;

import iristk.speech.RecHyp;
import iristk.speech.RecResult;
import iristk.speech.RecognizerListener;
import iristk.util.Graph;
import iristk.util.Utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;

import javax.sound.sampled.AudioFormat;


public class Parser implements RecognizerListener {

	private Stack<Edge> agenda = new Stack<Edge>();
	private Chart activeChart = new Chart();
	private Chart passiveChart = new Chart();
	
	private static HashMap<String,ABNFGrammar> cachedGrammars = new HashMap<>();

	public boolean onlyPublicRules = true;
	private List<Word> words;
	private State state;
	
	private HashMap<String,Grammar> grammars = new HashMap<>();
	private HashSet<String> activatedGrammars = new HashSet<>();;
	
	//public Parser(Grammar grammar) {
	//	addGrammar(grammar);
	//}
	
	public Parser() {
	}
	
	private class State {
		private Stack<Edge> agenda;
		private Chart activeChart;
		private Chart passiveChart;
	}
		
	private void saveState() {
		state = new State();
		state.agenda = new Stack<Edge>();
		state.agenda.addAll(agenda);
		state.activeChart = new Chart(activeChart);
		state.passiveChart = new Chart(passiveChart);
	}
	
	private void restoreState() {
		agenda = new Stack<Edge>();
		agenda.addAll(state.agenda);
		activeChart = new Chart(state.activeChart);
		passiveChart = new Chart(state.passiveChart);
	}
	
	private void combine(Edge active, Edge passive) {
		List<Edge> newEdges = active.matches(passive);
		if (newEdges != null) {
			for (Edge newEdge : newEdges) {
				agenda.add(newEdge);
				if (newEdge instanceof RuleEdge && ((RuleEdge)newEdge).isPotentiallyPassive()) {
					RuleEdge re = ((RuleEdge)newEdge).makePassive();
					agenda.add(re);
				}
			}
		}
	}
	
	private void processAgenda() {
		while (!agenda.isEmpty()) {
			Edge edge = agenda.pop();
			if (edge.isPassive()) {
				ArrayList<Edge> activeEdges = activeChart.get(edge.getBegin());
				int i = 0;
				while (i < activeEdges.size()) {
					Edge active = activeEdges.get(i);
					combine(active, edge);
					i++;
				}
				//System.out.println(edge);
				passiveChart.put(edge.getBegin(), edge);
			}
			if (edge.isActive()) {
				for (Edge passive : passiveChart.get(edge.getEnd())) {
					combine(edge, passive);
				}
				activeChart.put(edge.getEnd(), edge);
			}
		} 
	}
	
	private void initRules(Grammar grammar, boolean putOnAgenda) {
		for (Object rule : grammar.getRules()) {
			for (int i = 0; i < words.size(); i++) {
				RuleEdge edge = new RuleEdge(i, grammar, rule);
				activeChart.put(i, edge);
				if (putOnAgenda) {
					agenda.push(edge);
				}
			}
 		}
	}
	
	private void initWords(List<Word> words) {
		this.words = words;
		activeChart.clear();
		passiveChart.clear();
		agenda.clear();
		for (int i = 0; i < words.size(); i++) {
			agenda.push(new WordEdge(i, words.get(i)));
		}
	}

	private List<Edge> findBestEdges(Integer start, Integer end) {
		Graph<Integer,Edge> edgeGraph = new Graph<Integer,Edge>();
		for (Integer v : passiveChart.getVertices()) {
			for (Edge e : passiveChart.get(v)) {
				if (!onlyPublicRules || e instanceof WordEdge || (e instanceof RuleEdge && ((RuleEdge)e).isPublic())) {
					edgeGraph.addEdge(e, e.getBegin(), e.getEnd(), (1 + (e.getEnd() - e.getBegin())) - ((e.getLevel()) / 100.0f));
				}
			}
		}
		List<Edge> topEdges = edgeGraph.getShortestPath(start, end);
		return topEdges;
	}

	private ParseResult findBestResult(Integer start, Integer end) {
		List<Edge> topEdges = findBestEdges(start, end);
		ParseResult result = new ParseResult();
		ParseResult.Phrase wordPhrase = null;
		for (Edge e : topEdges) {
			if (e instanceof RuleEdge) {
				wordPhrase = null;
				result.add(new ParseResult.Phrase(((RuleEdge)e).getRuleId(), 
						((RuleEdge)e).getSem(), 
						e.getWords()));
			} else if (e instanceof WordEdge) {
				if (wordPhrase == null) {
					wordPhrase = new ParseResult.Phrase();
					result.add(wordPhrase);
				}
				wordPhrase.getWords().add(((WordEdge)e).getWord());
			}
		}
		return result;
	}
	
	private static List<Word> tokenize(String text) {
		ArrayList<Word> words = new ArrayList<Word>();
		for (String w : text.split(" ")) {
			words.add(new Word(w));
		}
		return words;
	}
	
	public ParseResult parse(List<Word> words) {
		initWords(words);
		for (Grammar grammar : getActivatedGrammars()) {
			initRules(grammar, false);
		}
		processAgenda();
		saveState();
		return findBestResult(0, words.size());
	}

	public ParseResult parse(String text) {
		return parse(tokenize(text));
	}
	
	public ParseResult parse(Word... words) {
		ArrayList<Word> wl = new ArrayList<Word>();
		for (Word w : words) {
			wl.add(w);
		}
		return parse(wl);
	}

	public static ABNFGrammar parseGrammarString(String text) throws IOException, GrammarException {
		if (cachedGrammars.containsKey(text)) {
			return cachedGrammars.get(text);
		} else {
			ABNFGrammar grammar = new ABNFGrammar(new ByteArrayInputStream(text.getBytes()));
			cachedGrammars.put(text, grammar);
			return grammar;
		}
	}
	
	public String find(String pattern, Object text) {
		if (text == null)
			return null;
		initWords(tokenize(text.toString()));
		for (Grammar grammar : getActivatedGrammars()) {
			initRules(grammar, false);
		}
		try {
			ABNFGrammar grammar = parseGrammarString("$findRuleX = " + pattern);
			initRules(grammar, false);
			saveState();
			processAgenda();
			RuleEdge rule = passiveChart.findRule("findRuleX");
			if (rule != null) {
				return Utils.listToString(rule.getWords());
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (GrammarException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String find(String pattern) {
		restoreState();
		try {
			ABNFGrammar grammar = parseGrammarString("$findRuleX = " + pattern);
			initRules(grammar, true);
			processAgenda();
			RuleEdge rule = passiveChart.findRule("findRuleX");
			if (rule != null) {
				return Utils.listToString(rule.getWords());
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (GrammarException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public boolean match(String pattern) {
		return find(pattern) != null;
	}
	
	public boolean match(String pattern, Object text) {
		return find(pattern, text) != null;
	}
	
	@Override
	public void recognitionResult(RecResult result) {
		//System.out.println("PARSER: " + activatedGrammars.size());
		if (result == null || getActivatedGrammars().size() == 0)
			return;
		if ((result.isFinal() || result.isPartial())) {
			//TODO: parse the individual words and assign confidence to concepts
			if (result.text != null && result.text.length() > 0) {
				if (result.sem == null || result.sem.size() == 0) {
					result.sem = parse(result.text).getSem();
				}
			}
			if (result.nbest != null && result.nbest.size() > 0) {
				for (RecHyp rechyp : result.nbest) {
					if (rechyp.sem == null || rechyp.sem.size() == 0) {
						rechyp.sem = parse(rechyp.text).getSem();
					}
				}
			}
		}
	}

	/*
	private Record parserRecord = new Record();
	{
		parserRecord.put("find", new DynamicValue() {
			@Override
			public Object getValue(Object... args) {
				if (args.length > 0 && args[0] != null) {
					return find(args[0].toString());
				}
				return null;
			}
		});
	}
	*/
	
	@Override
	public void speechSamples(byte[] samples, int pos, int len) {
	}

	@Override
	public void initRecognition(AudioFormat format) {
	}
	
	@Override
	public String toString() {
		return "Parser";
	}
	
	public static void main(String args[]) throws Exception {
		Parser parser = new Parser();
		//parser.addGrammar(new SrgsGrammar(new File("app/numbers/src/iristk/app/numbers/NumbersGrammar.xml")));
		//ParseResult result = parser.parse("one two three");
		parser.loadGrammar("test", new SRGSGrammar(new File("C:/Dropbox/KTH/Timeline/semantics_sv.xml")));
		parser.activateGrammar("test");
		ParseResult result = parser.parse("vad tror du om kängurun");
		System.out.println(result);
		//System.out.println(parser.parse("move the knight two steps"));
		//System.out.println(parser.find("move the", "move the pawn"));
		//System.out.println(parser.group(0));
	}
	
	private static class NomatchParser extends Parser {
		
		@Override
		public String find(String pattern) {
			return null;
		}
		
	}

	@Override
	public void startOfSpeech(float timestamp) {		
	}

	@Override
	public void endOfSpeech(float timestamp) {
	}

	public Chart getPassiveChart() {
		return passiveChart;
	}
	
	public List<Word> getWords() {
		return words;
	}

	/*
	 
	public void clearGrammars() {
		grammars.clear();
	}
	
	public void addGrammar(Grammar grammar) {
		grammars.add(grammar);
	}
	
	public void removeGrammar(Grammar grammar) {
		grammars.remove(grammar);
	}
	
	public List<Grammar> getGrammars() {
		return grammars;
	}
	
	  
	 */
	
	public void loadGrammar(String name, Grammar grammar) {
		grammars.put(name, grammar);
	}

	public void unloadGrammar(String name) {
		grammars.remove(name);
	}
	
	private List<Grammar> getActivatedGrammars() {
		List<Grammar> result = new ArrayList<>();
		for (String name : activatedGrammars) {
			if (grammars.containsKey(name))
				result.add(grammars.get(name));
		}
		return result;
	}

	public void activateGrammar(String name) {
		activatedGrammars.add(name);
	}

	public void deactivateGrammar(String name) {
		activatedGrammars.remove(name);
	}
	
}

