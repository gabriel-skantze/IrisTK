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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 
 * <p>A NameFilter is used to match the name of an event. 
 * The filter consists of a list semicolon-separated items. At least one of these items must match.
 * <p>Each item then consists of a list of space-separated include or exclude patterns. Exclude patterns start with "!".
 * <p>Example: "P1 !P2;P3 P4 !P5 !P6" means that either the event name must match P1 and must not match P2, or it must match either P3 or P4 but must not match P5 or P6. 
 * <p>If only Exclude patterns are used in a sequence, this means that anything that does not match any of these patterns is accepted. 
 * <p> 
 * <p>Each pattern may only contain characters, dots and stars. The pattern should match the whole name.
 * <p>dot (.)  : matches a dot literally
 * <p>star (*) : matches 0 or more characters, excluding dots
 * <p>double star (**): matches 0 or more characters, including dots
 *
 */
public class NameFilter {

	private static HashMap<String,NameFilter> cachedFilters = new HashMap<String,NameFilter>();
	
	/**
	 * A static filter that accepts anything
	 */
	public static final NameFilter ALL = NameFilter.compile("**");
	/**
	 * A static filter that accepts nothing 
	 */
	public static final NameFilter NONE = NameFilter.compile("");
	
	private List<FilterItem> filterItems;
	
	private NameFilter(String patt) {
		String[] parts = patt.trim().split(" *; *");
		filterItems = new ArrayList<FilterItem>(parts.length);
		for (int i = 0; i < parts.length; i++) {
			FilterItem item = new FilterItem(parts[i]);
			if (item.toString().length() > 0) {
				filterItems.add(item);
			}
		}
	}
	
	private NameFilter(NameFilter filter) {
		this.filterItems = new ArrayList<FilterItem>(filter.filterItems);
	}

	public boolean accepts(String name) {
		for (FilterItem item : filterItems) {
			if (item.accepts(name)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Creates a filter by parsing a pattern string
	 */
	public static synchronized NameFilter compile(String patt) {
		if (cachedFilters.containsKey(patt))
			return cachedFilters.get(patt);
		if (!patt.matches("[A-Za-z0-9_\\!\\.\\*; ]*"))
			throw new IllegalArgumentException("NamePattern illegal: " + patt);
		NameFilter filter = new NameFilter(patt);
		cachedFilters.put(patt, filter);
		return filter;
	}

	@Override
	public String toString() {
		String string = "";
		for (int i = 0; i < filterItems.size(); i++) {
			if (string.length() > 0)
				string += ";";
			string += filterItems.get(i).toString();
		}
		return string;
	}
	
	/**
	 * Combines this filter (F1) with another filter (F2) and returns a new filter (F3). F1 and F2 are combined with ";", which means that F3 accepts anything that matches at least one of F1 or F2.
	 */
	public NameFilter combine(NameFilter filter) {
		if (this.equals(ALL))
			return ALL;
		else if (filter.equals(ALL))
			return ALL;
		NameFilter result = new NameFilter(this);
		result.filterItems.addAll(new NameFilter(filter).filterItems);
		return result;
	}

	public boolean equals(NameFilter filter) {
		return this.toString().equals(filter.toString());
	}
	
	private static class FilterItem {
		
		Pattern[] patterns;
		String[] spatterns;
		boolean[] isInclude;
		boolean allExclude;
		
		public FilterItem(String pattern) {
			String[] parts = pattern.trim().split(" +");
			patterns = new Pattern[parts.length];
			spatterns = new String[parts.length];
			isInclude = new boolean[parts.length];
			allExclude = parts.length > 0;
			for (int i = 0; i < parts.length; i++) {
				String part = parts[i];
				spatterns[i] = part;
				isInclude[i] = !part.startsWith("!");
				if (isInclude[i])
					allExclude = false;
				if (part.startsWith("!")) 
					part = part.substring(1);
				part = part.replaceAll("\\.", "#DOT#");
				part = part.replaceAll("\\*\\*", "#DS#");
				part = part.replaceAll("\\*", "[^\\.]*");
				part = part.replaceAll("#DOT#", "\\\\.");
				part = part.replaceAll("#DS#", ".*");
				patterns[i] = Pattern.compile(part);
			}
		}

		boolean accepts(String name) {
			boolean accepts = allExclude;
			for (int i = 0; i < patterns.length; i++) {
				if (patterns[i].matcher(name).matches()) {
					accepts = isInclude[i];
				}
			}
			return accepts;
		}
		
		@Override
		public String toString() {
			String result = "";
			for (int i = 0; i < patterns.length; i++) {
				if (result.length() > 0) result += " ";
				result += spatterns[i];
			}
			return result;
		}
	}
	
	//public static void main(String[] args) {
	//	System.out.println(ALL.accepts("test"));
	//}
}
