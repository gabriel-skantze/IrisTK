package iristk.cfg;

import iristk.util.RandomList;

import java.util.ArrayList;
import java.util.List;

public class GrammarUtils {

	public static Object getRule(Grammar grammar, String ruleId) {
		for (Object rule : grammar.getRules()) {
			String rid = grammar.getRuleId(rule);
			if (rid.equals(ruleId))
				return rule;
		}
		return null;
	}
	
	public static List<String> getDependentRules(Grammar grammar, Object rule) {
		List<String> result = new ArrayList<>();
		getDependentRules(grammar, result, grammar.getMatches(rule));
		return result;
	}
	
	private static void getDependentRules(Grammar grammar, List<String> result, List<Object> matches) {
		for (Object matcher : matches) {
			if (grammar.isRuleRef(matcher)) {
				String ruleref = grammar.getRuleRef(matcher);
				if (!result.contains(ruleref))
					result.add(ruleref);
				getDependentRules(grammar, result, grammar.getMatches(getRule(grammar, grammar.getRuleRef(matcher))));
			} else if (grammar.isOneOf(matcher) || grammar.isItem(matcher)) {
				getDependentRules(grammar, result, grammar.getMatches(matcher));
			}
		}
	}

	public static String generate(Grammar grammar, String ruleId) {
		return generate(grammar, grammar.getMatches(getRule(grammar, ruleId)));
	}
	
	private static String generate(Grammar grammar, List<Object> matches) {
		StringBuilder result = new StringBuilder();
		for (Object matcher : matches) {
			result.append(generate(grammar, matcher));
		}
		return result.toString();
	}
	
	private static String generate(Grammar grammar, Object matcher) {
		if (grammar.isRuleRef(matcher)) {
			return generate(grammar, grammar.getRuleRef(matcher));
		} else if (grammar.isOneOf(matcher)) {
			return generate(grammar, RandomList.getRandom(grammar.getMatches(matcher)));
		} else if (grammar.isItem(matcher)) {
			int min = grammar.getMinRepeat(matcher);
			int max = grammar.getMaxRepeat(matcher);
			int repeat = min;
			if (max - min > 0) {
				repeat += RandomList.nextInt(1 + max - min);
			}
			StringBuilder result = new StringBuilder();
			for (int i = 0; i < repeat; i++) {
				result.append(generate(grammar, grammar.getMatches(matcher)));
			}
			return result.toString();
		} else if (grammar.isWord(matcher)) {
			return grammar.getWordString(matcher) + " ";
		}
		return "";
	}
	
}
