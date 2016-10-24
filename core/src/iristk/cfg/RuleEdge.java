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

import iristk.system.IrisUtils;

import java.util.LinkedList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;

public class RuleEdge extends Edge {
	
	private static Logger logger = IrisUtils.getLogger(RuleEdge.class);

	private Object rule;
	private MatchList matches;
	private List<RuleEdge> subRules = new LinkedList<RuleEdge>();
	private List<Word> words = new LinkedList<Word>();
	private int level;
	private Grammar grammar;
	private Object nativeSem = null;
	
	public RuleEdge(int i, Grammar grammar, Object rule) {
		this.grammar = grammar;
		this.rule = rule;
		this.begin = i;
		this.end = i;
		this.level = 0;
		matches = new MatchList(grammar.getMatches(rule));
		evalTags(this.matches);
	}
	
	public RuleEdge(RuleEdge active, Edge passive, MatchList matches) {
		this.rule = active.rule;
		this.grammar = active.grammar;
		this.begin = active.begin;
		this.end = passive.end;
		if (active.nativeSem != null)
			this.nativeSem = TagScript.cloneNativeObject(active.nativeSem);
		this.subRules.addAll(active.subRules);
		if (passive instanceof RuleEdge)
			this.subRules.add((RuleEdge)passive);
		this.words.addAll(active.getWords());
		this.words.addAll(passive.getWords());
		this.level = Math.max(active.getLevel(), passive.getLevel() + 1);
		this.matches = matches;
		evalTags(this.matches);
		//System.out.println(this);
	}
	
	private RuleEdge(RuleEdge active) {
		this.rule = active.rule;
		this.grammar = active.grammar;
		this.begin = active.begin;
		this.end = active.end;
		if (active.nativeSem != null)
			this.nativeSem = TagScript.cloneNativeObject(active.nativeSem);
		this.subRules.addAll(active.subRules);
		this.words.addAll(active.getWords());
		this.level = active.getLevel();
		this.matches = new MatchList(active.matches);
		for (int i = 0; i < matches.size();) {
			if (grammar.isTag(matches.get(i))) {
				i++;
			} else {
				matches.remove(i);
			}
		}
		evalTags(this.matches);
	}
	
	private void evalTags(MatchList matches) {
		for (int i = 0; i < matches.size();) {
			if (grammar.isTag(matches.get(i))) {
				String tagScript = grammar.getTagScript(matches.get(i));
				if (tagScript.length() > 0) {
					nativeSem = TagScript.eval(tagScript, this, nativeSem);
				} 
				matches.remove(i);
			} else {
				break;
			}
		}
		if (isPassive() && nativeSem == null) {
			String result = "";
			for (Word w : words) {
				result += w.toString() + " ";
			}
			nativeSem = result.trim();
		}
	}
	
	public boolean isPublic() {
		return grammar.isPublic(rule);
	}
	
	private void matches(MatchList matches, Edge passive, List<Edge> result) {
		evalTags(matches);
		if (matches.size() == 0)
			return;
		Object token = matches.get(0);
		List<Object> rest = matches.subList(1, matches.size());
		if (grammar.isOneOf(token)) {
			for (Object item : grammar.getMatches(token)) {
				MatchList newMatches = new MatchList(matches, rest);
				newMatches.add(0, item);
				matches(newMatches, passive, result);
			}
		} else if (grammar.isItem(token)) {
			if (matches.getMinRepeat(token) == 0) {
				matches(new MatchList(matches, rest), passive, result);
			} 
			MatchList newMatches = new MatchList(matches, rest);
			if (matches.getMaxRepeat(token) > 1) {
				newMatches.decrRepeat(token);
				newMatches.add(0, token);
			}
			newMatches.addAll(0, grammar.getMatches(token));
			matches(newMatches, passive, result);
		} else if (grammar.isRuleRef(token)) {
			String ref = grammar.getRuleRef(token);
			if (ref.equals("GARBAGE")) {
				// Discard the rule (zero match)
				matches(new MatchList(matches, rest), passive, result);
				// Consume the passive edge
				RuleEdge resEdge = new RuleEdge(this, passive, new MatchList(matches, new LinkedList<Object>(matches)));
				result.add(resEdge);
			} else if (passive instanceof RuleEdge) {
				if (((RuleEdge)passive).getRuleId().equals(ref)) {
					RuleEdge resEdge = new RuleEdge(this, passive, new MatchList(matches, rest));
					result.add(resEdge);
				}
			}
		} else if (grammar.isWord(token)) {
			if (passive instanceof WordEdge) {
				WordEdge we = (WordEdge) passive;
				if (wordMatches(grammar.getWordString(token), we.getWord())) {
					RuleEdge resEdge = new RuleEdge(this, passive, new MatchList(matches, rest));
					result.add(resEdge);
				}
			}
		} else if (grammar.isTag(token)) {
			logger.error("Illegal tag placement: " + grammar.getTagScript(token));
		} else {
			logger.error("Problem recognizing " + token.getClass());
		}
	}

	private boolean wordMatches(String token, Word word) {
		String pattern = token.replaceAll("_", ".*");
		//if (token.startsWith("k") && word.getWordString().startsWith("k")) {
		//	System.out.println(token + " " + word.getWordString() + " " + word.getWordString().matches("(?i:" + pattern + ")"));
		//}
		//System.out.println(word.getWordString() + " " + pattern);
		//return word.getWordString().matches("(?i:" + pattern + ")");
		return word.getWordString().toUpperCase().matches(pattern.toUpperCase());
	}

	@Override
	public List<Edge> matches(Edge passive) {
		if (matches.size() > 0) {
			List<Edge> result = new LinkedList<Edge>();
			matches(matches, passive, result);
			return result;
		} else {
			return null;
		}
	}
	
	@Override
	public boolean isPassive() {
		return matches.size() == 0;
	}
	
	public boolean isPotentiallyPassive() {
		boolean result = false;
		for (Object token : matches) {
			if ((grammar.isItem(token) && matches.getMinRepeat(token) == 0)) {
				result = true;
			} else if (grammar.isTag(token)) {
			} else {
				return false;
			}
		}
		return result;
	}
	
	public RuleEdge makePassive() {
		return new RuleEdge(this);
	}
	
	@Override
	public boolean isActive() {
		return matches.size() > 0;
	}
	
	@Override
	public String toString() {
		return "RuleEdge{" + getRuleId() + ":" + begin + ":" + end + ":" + matches + "}" + getSem();
	}

	@Override
	public List<Word> getWords() {
		return words;
	}

	public String getWordString() {
		StringBuilder res = new StringBuilder();
		for (Word word : words) {
			res.append(word + " ");
		}
		return res.toString().trim();
	}
	
	public String getRuleId() {
		return grammar.getRuleId(rule);
	}
	
	@Override
	public int getLevel() {
		return level;
	}

	public Object getNativeSem() {
		return nativeSem;
	}
	
	public Object getSem() {
		//return TagScript.nativeSemToRecord(getNativeSem());
		return TagScript.jsObjectToSem(getNativeSem());
	}

	public List<RuleEdge> getSubRules() {
		return subRules;
	}
	
	private class MatchList extends LinkedList<Object> {

		private HashMap<Object,Integer> minRepeat;
		private HashMap<Object,Integer> maxRepeat;
		
		public MatchList(List<Object> matches) {
			this.maxRepeat = new HashMap<>();
			this.minRepeat = new HashMap<>();
			addAll(matches);
		}

		public MatchList(MatchList clone, List<Object> matches) {
			this.maxRepeat = new HashMap<>(clone.maxRepeat);
			this.minRepeat = new HashMap<>(clone.minRepeat);
			addAll(matches);
		}

		public int getMinRepeat(Object token) {
			if (minRepeat.containsKey(token))
				return minRepeat.get(token);
			else
				return grammar.getMinRepeat(token);
		}
		
		public int getMaxRepeat(Object token) {
			if (maxRepeat.containsKey(token))
				return maxRepeat.get(token);
			else
				return grammar.getMaxRepeat(token);
		}
		
		public void decrRepeat(Object token) {
			if (!maxRepeat.containsKey(token)) 
				maxRepeat.put(token, grammar.getMaxRepeat(token) - 1);
			else
				maxRepeat.put(token, maxRepeat.get(token) - 1);
			if (!minRepeat.containsKey(token)) 
				minRepeat.put(token, Math.max(0, grammar.getMinRepeat(token) - 1));
			else
				minRepeat.put(token, Math.max(0, minRepeat.get(token) - 1));
		}
		
		@Override
		public String toString() {
			String result = "";
			for (Object match : this) {
				if (result.length() > 0)
					result += ", ";
				result += ABNFGrammar.matchToString(grammar, match);
			}
			return "[" + result + "]";
		}
		
	}
	
}
