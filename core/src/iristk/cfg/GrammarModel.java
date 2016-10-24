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

import iristk.util.Language;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public abstract class GrammarModel implements Grammar {

	private Language language;
	private String root;
	private List<Object> rules = new ArrayList<Object>();
	protected URI uri;

	public GrammarModel() {
	}

	public GrammarModel(Grammar grammar) {
		this.language = grammar.getLanguage();
		this.root = grammar.getRoot();
		for (Object rule : grammar.getRules()) {
			rules.add(new Rule(grammar.getRuleId(rule), grammar.isPublic(rule), convert(grammar, grammar.getMatches(rule))));
		}
	}
	
	private List<Object> convert(Grammar grammar, List<Object> matches) {
		List<Object> result = new ArrayList<Object>();
		for (Object match : matches) {
			result.add(convert(grammar, match));
		}
		return result;
	}
	
	private Object convert(Grammar grammar, Object match) {
		if (grammar.isItem(match)) {
			Item group = new Item(convert(grammar, grammar.getMatches(match)));
			group.setRepeat(grammar.getMinRepeat(match), grammar.getMaxRepeat(match));
			return group;
		} else if (grammar.isOneOf(match)) {
			OneOf oneof = new OneOf(convert(grammar, grammar.getMatches(match)));
			return oneof;
		} else if (grammar.isRuleRef(match)) {
			return new RuleRef(grammar.getRuleRef(match));
		} else if (grammar.isWord(match)) {
			return grammar.getWordString(match);
		} else if (grammar.isTag(match)) {
			return new Tag(grammar.getTagScript(match));
		} else {
			throw new RuntimeException("Cannot convert " + match);
		}
	}
	
	protected void addRule(String ruleId, boolean isPublic, List<Object> matches) {
		rules.add(new Rule(ruleId, isPublic, matches));
	}
	
	protected void setLanguage(Language language) {
		this.language = language;
	}
	
	protected void setRoot(String root) {
		this.root = root;
	}
	
	@Override
	public List<Object> getRules() {
		return rules;
	}

	@Override
	public String getRuleId(Object rule) {
		return ((Rule)rule).getId();
	}

	@Override
	public boolean isPublic(Object rule) {
		return ((Rule)rule).isPublic();
	}

	@Override
	public List<Object> getMatches(Object ruleOrMatcher) {
		return new ArrayList<Object>((List)ruleOrMatcher);
	}

	@Override
	public boolean isOneOf(Object matcher) {
		return (matcher instanceof OneOf);
	}

	@Override
	public boolean isRuleRef(Object matcher) {
		return (matcher instanceof RuleRef);
	}

	@Override
	public boolean isItem(Object matcher) {
		return (matcher instanceof Item);
	}

	@Override
	public int getMinRepeat(Object matcher) {
		return ((Item)matcher).getMinRepeat();
	}

	@Override
	public int getMaxRepeat(Object matcher) {
		return ((Item)matcher).getMaxRepeat();
	}

	@Override
	public String getRuleRef(Object matcher) {
		return ((RuleRef)matcher).getRef();
	}

	@Override
	public boolean isWord(Object matcher) {
		return matcher instanceof String;
	}

	@Override
	public String getWordString(Object word) {
		return word.toString();
	}
	
	@Override
	public boolean isTag(Object matcher) {
		return matcher instanceof Tag;
	}

	@Override
	public String getTagScript(Object matcher) {
		return ((Tag)matcher).getScript();
	}

	@Override
	public Language getLanguage() {
		return language;
	}

	@Override
	public String getRoot() {
		return root;
	}
	
	@Override
	public Float getWeight(Object matcher) {
		if (matcher instanceof Item) {
			return ((Item)matcher).getWeight();
		} else { 
			return null;
		}
	}
	
	public void include(GrammarModel grammar) {
		for (Object rule : grammar.getRules()) {
			((Rule)rule).ruleId = grammar.qualify(((Rule)rule).ruleId);
			rules.add(rule);
		}
	}
	
	public String includeRoot() {
		return qualify(getRoot());
	}
	
	private String qualify(String name) {
		return (name + "_" + uri.toString().hashCode()).replaceAll("[.:\\-]", "");
	}
	
	public static boolean isSpecial(String ref) {
		return (ref.equals("NULL") || ref.equals("VOID") || ref.equals("GARBAGE"));
	}
	
	public static class Tag {

		private String script;
		
		public Tag(String script) {
			this.script = script;
		}
		
		public String getScript() {
			return script;
		}
		
	}
	
	public static class RuleRef {

		private String ref;
		
		public RuleRef(String ref) {
			this.ref = ref;
		}
		
		public String getRef() {
			return ref;
		}
		
	}
	
	public static class Rule extends ArrayList<Object> {

		private boolean isPublic;
		private String ruleId;

		public Rule(String ruleId, boolean isPublic, List<Object> matches) {
			super(matches);
			this.ruleId = ruleId;
			this.isPublic = isPublic;
		}

		public boolean isPublic() {
			return isPublic;
		}

		public String getId() {
			return ruleId;
		}
		
	}
	
	public static class OneOf extends ArrayList<Object> {

		public OneOf(List<Object> matches) {
			super(matches);
		}
		
		public OneOf() {
		}
		
	}
	
	public static class Item extends ArrayList<Object> {
				
		private int minRepeat;
		private int maxRepeat;
		private Float weight;

		public Float getWeight() {
			return weight;
		}

		public void setWeight(Float weight) {
			this.weight = weight;
		}

		public Item(List<? extends Object> matches) {
			super(matches);
			this.minRepeat = 1;
			this.maxRepeat = 1;
		}
		
		public void setRepeat(int min, int max) {
			this.minRepeat = min;
			this.maxRepeat = max;
		}
		
		public int getMaxRepeat() {
			return maxRepeat;
		}

		public int getMinRepeat() {
			return minRepeat;
		}
		
	}

}
