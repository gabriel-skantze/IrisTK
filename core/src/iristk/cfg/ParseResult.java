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

import iristk.util.Record;

import java.util.ArrayList;
import java.util.List;

public class ParseResult extends ArrayList<ParseResult.Phrase> {
	
	public static class Phrase {
		
		private Object sem;
		private List<Word> words;
		private String ruleId;
		
		public Phrase() {
			this.words = new ArrayList<Word>();
		}
		
		public Phrase(String ruleId, Object sem, List<Word> list) {
			this();
			this.words.addAll(list);
			this.sem = sem;
			this.ruleId = ruleId;
		}

		public List<Word> getWords() {
			return words;
		}

		public Object getSem() {
			return sem;
		}
		
		public String getRuleId() {
			return ruleId;
		}
		
		public String getWordString() {
			StringBuilder res = new StringBuilder();
			for (Word word : words) {
				res.append(word + " ");
			}
			return res.toString().trim();
		}
		
		@Override
		public String toString() {
			if (sem == null)
				return words.toString();
			else
				return words.toString() + sem.toString();
		}
		
	}
	
	public String getWordString() {
		StringBuilder res = new StringBuilder();
		for (Phrase phrase : this) {
			for (Word word : phrase.words) {
				res.append(word + " ");
			}
		}
		return res.toString().trim();
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (this.size() > 1) {
			for (Phrase phrase : this) {
				sb.append("PART: \"" + phrase.getWordString() + "\"");
				Object sem = phrase.getSem();
				if (sem != null && sem instanceof Record)
					sb.append(" : \n" + ((Record)sem).toStringIndent() + "\n");
				else if (sem != null)
					sb.append(" : " + sem + "\n");
				else
					sb.append(" : null\n");
			}
			sb.append("FULL");
		} else {
			sb.append("FULL : \"" + getWordString() + "\"");
		}
		Object sem = getSem();
		if (sem != null && sem instanceof Record)
			sb.append(" : \n" + ((Record)sem).toStringIndent() + "\n");
		else if (sem != null)
			sb.append(" : " + sem + "\n");
		else
			sb.append(" : null\n");
		return sb.toString().trim();
	}
	
	/**
	 * Unifies the semantics of all phrases
	 */
	public Record getSem() {
		Record sem = new Record();
		for (Phrase phrase : this) {
			if (phrase.getSem() instanceof Record) {
				Record psem = (Record)phrase.getSem();
				sem.adjoin(psem);
				/*
				for (String field : psem.getFields()) {
					if (sem.get(field) != null && sem.get(field) instanceof List && psem.get(field) instanceof List) {
						List merged = new ArrayList((List)sem.get(field));
						merged.addAll((List)psem.get(field));
						sem.put(field, merged);
					} else {
						sem.put(field, psem.get(field));
					}
				}
				*/
			}
		}
		return sem;
	}
	
	/*
	public static String objectToString(Object obj) {
		if (obj instanceof NativeObject) {
			String result = "{";
			NativeObject no = (NativeObject) obj;
			boolean first = true;
			for (Object pid : NativeObject.getPropertyIds(no)) {
				if (!first) result += ",";
				result += pid + ":" + objectToString(NativeObject.getProperty(no,pid.toString()));
				first = false;
			}
			result += "}";
			return result;
		} else {
			return obj.toString();
		}
	}
	*/
	
	/*
	public static String semToString(Object sem) {
		if (sem == null) {
			return "";
		} else if (sem.getSem().size() > 0) {
			String result = "{";
			boolean first = true;
			for (Sem c : sem.getSem()) {
				if (!first)
					result += ",";
				result += c.getField() + ":" + semToString(c);
				first = false;
			}
			result += "}";
			return result;
		} else {
			return sem.getValue();
		}
	}
	*/

	/*
	public List<Sem> getSemList() {
		List<Sem> result = new ArrayList<Sem>();
		for (ParseResult.Phrase phrase : this) {	
			if (phrase.getSem() != null) {
				try {
					result.add(phrase.getSem());
				} catch (IllegalArgumentException e) {
					System.err.println("Cannot translate " + phrase.getSem() + " to concept");
				}
			}
		}
		return result;
	}
	*/
	
	public float getSemCoverage() {
		float result = 0;
		float tot = 0;
		for (ParseResult.Phrase phrase : this) {
			if (phrase.getSem() != null) {
				result += phrase.getWords().size();
			}
			tot +=  phrase.getWords().size();
		}
		return result / tot;
	}
	
}
