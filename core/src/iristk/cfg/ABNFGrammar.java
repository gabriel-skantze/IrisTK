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
import iristk.util.ParsedInputStream;
import iristk.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.output.ByteArrayOutputStream;

/**
  
Extensions to ABNF:

*			0- repeat
+			1- repeat
_			Matches any word
red_		Matches all words that starts with "red"
 
 */

public class ABNFGrammar extends GrammarModel {
	
	public ABNFGrammar() {
	}
	
	public ABNFGrammar(InputStream is) throws IOException, GrammarException {
		readInputStream(is);
	}
	
	public ABNFGrammar(File file) throws IOException, GrammarException {
		this(file.toURI());
	}
	
	public ABNFGrammar(URI uri) throws IOException, GrammarException {
		this.uri = uri;
		readInputStream(uri.toURL().openStream());
	}
	
	public ABNFGrammar(String grammarString) throws IOException, GrammarException {
		readInputStream(new ByteArrayInputStream(grammarString.getBytes(StandardCharsets.UTF_8)));
	}
	
	public ABNFGrammar(Grammar grammar) {
		super(grammar);
	}
	
	private void readInputStream(InputStream is) throws IOException, GrammarException {
		boolean parsed = false;
		String rest = null;
		ParsedInputStream reader = new ParsedInputStream(is);
		String line = "";
		while (reader.available() > 0) {
			line += reader.readTo(';');
			int sb = StringUtils.countMatches(line, '{');
			int eb = StringUtils.countMatches(line, '}');
			if (sb == eb) {
				line = line.trim();
				if (line.length() > 0) {
					line = line.replace("\n", "");
					line = line.replace("\r", "");
					if (parseLine(line)) {
						parsed = true;
					} else if (rest == null) {
						rest = line;
					}
				}
				line = "";
			} else {
				line += ";";
			}
		}
		if (getLanguage() == null)
			setLanguage(Language.ENGLISH_US);
		if (!parsed) {
			if (rest == null && line.length() > 0) {
				rest = line;
			}
			if (rest != null && rest.length() > 0) {
				parseLine("root $root");
				parseLine("$root = " + rest);
			}
		}
	}
		
	public boolean parseLine(String line) throws IOException, GrammarException {
		if (line.startsWith("language")) {
			setLanguage(new Language(line.substring(line.lastIndexOf(" ")).trim()));
			return true;
		} else if (line.startsWith("root")) {
			setRoot(line.substring(line.lastIndexOf(" ")).trim().substring(1));
			return true;
		} else if (line.startsWith("public $") || line.startsWith("$")) {
			Matcher m = Pattern.compile("(public +)?\\$([^ =]+) *= *(.*)").matcher(line);
			if (m.matches()) {
				boolean isPublic = (m.group(1) != null);
				String ruleId = m.group(2);
				String matchStr = m.group(3);
				addRule(ruleId, isPublic, parseMatches(matchStr));
			}
			return true;
		} else if (line.startsWith("#")) {
			return true; 
		} else {
			return false;
		}
	}
	
	private void checkBalance(String str, char c1, char c2) throws GrammarException {
		if (StringUtils.countMatches(str, c1) != StringUtils.countMatches(str, c2)) {
			throw new GrammarException("Unbalanced '" + c1 + "' and '" + c2 + "' in: " + str);
		}
	}
	
	private List<Object> parseMatches(String matches) throws IOException, GrammarException {
		//System.out.println(matches);
		checkBalance(matches, '{', '}');
		checkBalance(matches, '(', ')');
		checkBalance(matches, '[', ']');
		checkBalance(matches, '<', '>');
		
		//System.out.println("#" + matches + "#");
		matches = matches.trim();
		List<String> groups = split(matches, "|");
		if (groups.size() > 1) {
			OneOf oneof = new OneOf();
			for (String gr : groups) {
				List<Object> grl = parseMatches(gr);
				if (grl.size() == 1) {
					oneof.add(grl.get(0));
				} else {
					oneof.add(new Item(grl));
				}
			}
			List<Object> result = new ArrayList<Object>();
			result.add(oneof);
			return result;
		}
		List<Object> result = new ArrayList<Object>();
		groups = split(matches, " ");
		if (groups.size() > 1) {
			for (String gr : groups) {
				List<Object> grl = parseMatches(gr);
				if (grl.size() == 1) {
					result.add(grl.get(0));
				} else {
					result.add(new Item(grl));
				}
			}
		} else {
			String group = groups.get(0);
			if (group.startsWith("(")) {
				List<Object> tag = null;
				if (group.endsWith("}")) {
					Matcher m = Pattern.compile("\\{[^\\(\\{\\}\\)]*\\}$").matcher(group);
					m.find();
					tag = parseMatches(m.group(0));
					group = m.replaceFirst("").trim();
				}
				List<Object> list = parseMatches(group.substring(1, group.length()-1));
				if (list.size() == 1)
					result.add(list.get(0));
				else
					result.add(new Item(list));
				if (tag != null)
					result.addAll(tag);
			} else if (group.startsWith("$<")) {
				String ref = group.replace("$<", "").replace(">", "").trim();
				try {
					ABNFGrammar include = new ABNFGrammar(uri == null ? new URI(ref) : uri.resolve(ref));
					include(include);
					result.add(new RuleRef(include.includeRoot()));
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
			} else if (group.startsWith("$")) {
				result.add(new RuleRef(group.substring(1)));
			} else if (group.startsWith("{")) {
				if (!group.endsWith("}"))
					throw new GrammarException("Bad expression: " + group);
				result.add(new Tag(group.substring(1, group.length()-1)));
			} else if (group.startsWith("[")) {
				if (!group.endsWith("]"))
					throw new GrammarException("Bad expression: " + group);
				List<Object> list = parseMatches(group.substring(1, group.length()-1));
				Item optional = new Item(list);
				optional.setRepeat(0, 1);
				result.add(optional);
			} else {
				result.add(group);
			}
		}
		return result;
	}

	private List<String> split(String string, String split) {
		List<String> result = new ArrayList<String>();
		String group = "";
		int para = 0;
		int cpara = 0;
		int spara = 0;
		for (int i = 0; i < string.length(); i++) {
			String c = string.substring(i, i+1);
			if (c.equals(split) && para == 0 && cpara == 0 && spara == 0) {
				result.add(group.trim());
				group = "";
			} else {
				if (c.equals("(")) {
					para++;
				} else if (c.equals(")")) {
					para--;
				} else if (c.equals("{")) {
					cpara++;
				} else if (c.equals("}")) {
					cpara--;
				} else if (c.equals("[")) {
					spara++;
				} else if (c.equals("]")) {
					spara--;
				}
				group += c;
			}
		}
		result.add(group.trim());
		return result;
	}

	public static String matchToString(Grammar grammar, Object match) {
		if (grammar.isItem(match)) {
			String content = matchToString(grammar, grammar.getMatches(match));
			int min = grammar.getMinRepeat(match);
			int max = grammar.getMaxRepeat(match);
			if (min == 1 && max == 1) {
				return "(" + content + ")";
			} else if (min == 0 && max == 1) {
				return "[" + content + "]";
			} else {
				return "(" + content + ") " + "<" + min + "-" + (max == INFINITY ? "" : max) + ">";
			}
		} else if (grammar.isOneOf(match)) {
			String result = "";
			boolean first = true;
			for (Object part : grammar.getMatches(match)) {
				if (!first) result += " | ";
				result += matchToString(grammar, part);
				first = false;
			}
			return "(" + result.trim() + ")";
		} else if (grammar.isRuleRef(match)) {
			return "$" + grammar.getRuleRef(match);
		} else if (grammar.isWord(match)) {
			return grammar.getWordString(match);
		} else if (grammar.isTag(match)) {
			return "{" + grammar.getTagScript(match) + "}";
		} else if (match instanceof List) {
			String result = "";
			for (Object part : (List)match) {
				result += matchToString(grammar, part) + " ";
			}
			return result.trim();
		} else {
			throw new RuntimeException("Cannot convert " + match);
		}
	}

	@Override
	public void marshal(OutputStream out) {
		PrintStream ps = new PrintStream(out);
		ps.println("#ABNF 1.0 UTF-8;\n");
		if (getLanguage() != null)
			ps.println("language " + getLanguage() + ";");
		if (getRoot() != null)
			ps.println("root $" + getRoot() + ";");
		ps.println();
		for (Object rule : getRules()) {
			if (isPublic(rule)) 
				ps.print("public ");
			ps.println("$" + getRuleId(rule) + " = " + matchToString(this, getMatches(rule)) + ";");
			ps.println();
		}
	}
	
	@Override
	public String toString() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		marshal(baos);
		try {
			return new String(baos.toByteArray(), "utf-8");
		} catch (UnsupportedEncodingException e) {
			return e.getMessage();
		}
	}

	public static void main(String[] args) throws Exception {
		new ABNFGrammar("okay").marshal(System.out);
		//new ABNFGrammar(new SrgsGrammar(new File("app/example/src/app/example/grammar.xml"))).marshal(System.out);;
		//new SrgsGrammar(new ABNFGrammar(new File("app/social/name_grammar.abnf"))).marshal(System.out);;
		//ContextFreeGrammar cfg = new ABNFGrammar(new File("app/chess/src/iristk/app/chess/ChessGrammar.abnf"));
		//Grammar cfg = new SrgsGrammar(new ABNFGrammar(new File("app/chess/src/iristk/app/chess/ChessGrammar.abnf")));
		//Grammar cfg = new ABNFGrammar(new SrgsGrammar(new File("app/chess/src/iristk/app/chess/ChessGrammar.xml")));
		//cfg.marshal(System.out);
	}
}
