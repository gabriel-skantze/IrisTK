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
package iristk.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Replacer implements ReplaceFun {
	
	final Pattern pattern;
	final SkipReplacer skipReplacer;
	
	public Replacer(String pattern) {
		this.pattern = Pattern.compile(pattern, Pattern.DOTALL);
		this.skipReplacer = null;
	}
	
	public Replacer(String pattern, String skipPattern) {
		this.pattern = Pattern.compile(pattern);
		this.skipReplacer = new SkipReplacer(skipPattern);
	}
	
	
	public static String replaceAll(String str, String regexp, ReplaceFun replaceFun) {
		return new Replacer(regexp){
			@Override
			public String replace(Matcher matcher) {
				return replaceFun.replace(matcher);
			}}.replaceAll(str);
	}
	
	private static class SkipReplacer extends Replacer {

		public SkipReplacer(String pattern) {
			super(pattern);
		}

		@Override
		public String replace(Matcher matcher) {
			int length = matcher.group().length();
			StringBuilder builder = new StringBuilder(length);
			for (int i = 0; i < length; i++) {
				builder.append(' ');
			}
			return builder.toString();
		}
		
	}
	
	public String replaceIter(String expr) {
		String result = replaceAll(expr);
		while (!result.equals(expr)) {
			expr = result;
			result = replaceAll(expr);
		}
		return result;
	}
	
	public String replaceAll(String expr) {
		if (skipReplacer != null) {
			return replaceAllSkip(expr);
		} else {
			boolean find = true;
			Matcher matcher = pattern.matcher(expr);
			String newExpr = expr;
			int adj = 0;
			while (find) {
				find = matcher.find();
				if (find) {
					String repl = replace(matcher);
					newExpr = newExpr.substring(0, matcher.start() + adj) + repl + newExpr.substring(matcher.end() + adj, newExpr.length());
					adj += (repl.length() - matcher.group().length());
				}
			}
			return newExpr;
		}
	}
	
	private String replaceAllSkip(String expr) {
		boolean find = true;
		Matcher matcher = pattern.matcher(expr);
		Matcher skipMatcher = pattern.matcher(skipReplacer.replaceIter(expr));
		String newExpr = expr;
		int adj = 0;
		while (find) {
			find = skipMatcher.find();
			if (find) {
				do {
					matcher.find();
				} while (matcher.start() != skipMatcher.start());
				String repl = replace(matcher);
				newExpr = newExpr.substring(0, matcher.start() + adj) + repl + newExpr.substring(matcher.end() + adj, newExpr.length());
				adj += (repl.length() - matcher.group().length());
			}
		}
		return newExpr;
	}
	
	/**
	 * Splits the string at the next unbalanced ending parenthesis (exclusive)
	 * "a (b)) c" => ["a (b)", ") c"]
	 */
	public static String[] paraSplit(String str) {
		int para = 0;
		String rest = "";
		for (int i = 0; i < str.length(); i++) {
			if (str.substring(i, i+1).equals("(")) {
				para++;
			} else if (str.substring(i, i+1).equals(")")) {
				para--;
				if (para < 0) {
					rest = str.substring(i);
					str = str.substring(0, i);
					break;
				}
			} 
		}
		return new String[] {str, rest};
	}
	
}


