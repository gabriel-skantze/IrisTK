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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import iristk.util.Converters;
import iristk.util.Language;
import iristk.util.Utils;
import iristk.xml.XmlMarshaller;
import iristk.xml.XmlUtils;
import iristk.xml.srgs.Item;
import iristk.xml.srgs.ObjectFactory;
import iristk.xml.srgs.OneOf;
import iristk.xml.srgs.Rule;
import iristk.xml.srgs.Ruleref;
import iristk.xml.srgs.ScopeDatatype;
import iristk.xml.srgs.SpecialDatatype;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;

import org.xml.sax.SAXException;

public class SRGSGrammar implements Grammar {

	private static final String SRGSNS = "http://www.w3.org/2001/06/grammar";

	public static String xmlXsd;
	public static String srgsXsd;
	
	static {
		try {
			xmlXsd = Utils.readString(XmlUtils.class.getResourceAsStream("xml.xsd"));
			srgsXsd = Utils.readString(XmlUtils.class.getResourceAsStream("srgs.xsd"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private XmlMarshaller<JAXBElement<iristk.xml.srgs.Grammar>> srgsMarshaller = new XmlMarshaller<>("iristk.xml.srgs");
	iristk.xml.srgs.Grammar grammar;
	private ObjectFactory srgsFactory = new ObjectFactory();
	
	public SRGSGrammar(Language lang) {
		grammar = new iristk.xml.srgs.Grammar();
		grammar.setVersion("1.0");
		grammar.setLang(lang.getCode());
		grammar.setTagFormat("semantics/1.0");
	}
	
	public SRGSGrammar(URI uri) {
		try {
			grammar = srgsMarshaller.unmarshal(uri.toURL()).getValue();
		} catch (JAXBException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
	public SRGSGrammar(File file) throws FileNotFoundException, JAXBException {
		grammar = srgsMarshaller.unmarshal(file).getValue();
	}
	
	public SRGSGrammar(String grammarString) throws JAXBException, SAXException, IOException {
		validate(grammarString);
		grammar = srgsMarshaller.unmarshal(grammarString).getValue();
	}
	
	public SRGSGrammar(Grammar cfg) {
		grammar = new iristk.xml.srgs.Grammar();
		grammar.setVersion("1.0");
		grammar.setLang(cfg.getLanguage().getCode());
		grammar.setTagFormat("semantics/1.0");
		for (Object rule : cfg.getRules()) {
			addRule(cfg, rule, cfg.getRuleId(rule).equals(cfg.getRoot()), cfg.isPublic(rule));
		}
	}
	
	private Serializable createMatch(Grammar cfg, Object matcher, Class scope) {
		if (cfg.isItem(matcher)) {
			Item item = new Item();
			int min = cfg.getMinRepeat(matcher);
			int max = cfg.getMaxRepeat(matcher);
			if (min != 1 || max != 1) {
				item.setRepeat(min + "-" + (max == INFINITY ? "" : max));
			}
			Float weight = cfg.getWeight(matcher);
			if (weight != null) {
				item.setWeight(weight.toString());
			}
			for (Object child : cfg.getMatches(matcher)) {
				item.getContent().add(createMatch(cfg, child, Item.class));
			}
			return new JAXBElement<Item>(new QName(SRGSNS, "item"), Item.class, scope, item);
		} else if (cfg.isOneOf(matcher)) {
			OneOf oneof = new OneOf();
			for (Object match : cfg.getMatches(matcher)) {
				Serializable child = createMatch(cfg, match, Item.class);
				Item item;
				if (child instanceof JAXBElement<?> && ((JAXBElement<?>)child).getValue() instanceof Item) {
					item = (Item)((JAXBElement<?>)child).getValue();
				} else {
					item = new Item();
					item.getContent().add(child);
				}
				oneof.getItem().add(item);
			}
			return new JAXBElement<OneOf>(new QName(SRGSNS, "one-of"), OneOf.class, scope, oneof);
		} else if (cfg.isRuleRef(matcher)) {
			Ruleref ruleref = new Ruleref();
			String ref = cfg.getRuleRef(matcher);
			if (ref.equals("GARBAGE")) {
				ruleref.setSpecial(SpecialDatatype.GARBAGE);
			} else if (ref.equals("NULL")) {
				ruleref.setSpecial(SpecialDatatype.NULL);
			} else if (ref.equals("VOID")) {
				ruleref.setSpecial(SpecialDatatype.VOID); 
			} else {
				ruleref.setUri("#" + ref);
			}
			return new JAXBElement<Ruleref>(new QName(SRGSNS, "ruleref"), Ruleref.class, scope, ruleref);
		} else if (cfg.isWord(matcher)) {
			return cfg.getWordString(matcher);
		} else if (cfg.isTag(matcher)) {
			return new JAXBElement<String>(new QName(SRGSNS, "tag"), String.class, scope, cfg.getTagScript(matcher));
		} else {
			System.err.println("Cannot recognize " + matcher);
			return null;
		}
	}
	
	private List<Object> filter(List<? extends Object> matches) {
		ArrayList<Object> result = new ArrayList<Object>();
		for (Object match : matches) {
			if (match instanceof String) {
				String str = ((String)match).trim();
				for (String word : str.split(" ")) {
					if (word.length() > 0)
						result.add(word);
				}
			} else {
				result.add(match);
			}
		}
		return result;
	}
	
	public void addRule(Grammar cfg, Object rule, boolean isRoot, boolean isPublic) {
		iristk.xml.srgs.Rule srule = new iristk.xml.srgs.Rule();
		srule.setId(cfg.getRuleId(rule));
		grammar.getRule().add(srule);
		if (isRoot)
			grammar.setRoot(srule);
		if (isPublic)
			srule.setScope(ScopeDatatype.PUBLIC);
		for (Object match : cfg.getMatches(rule)) {
			srule.getContent().add(createMatch(cfg, match, Rule.class));
		}
	}
	
	public void addRules(Grammar cfg) {
		for (Object rule : cfg.getRules()) {
			addRule(cfg, rule, false, cfg.isPublic(rule));
		}
	}

	@Override
	public List<Object> getRules() {
		return filter(grammar.getRule());
	}

	@Override
	public String getRuleId(Object token) {
		if (token instanceof Rule)
			return ((Rule)token).getId();
		else 
			return null;
	}

	@Override
	public boolean isPublic(Object token) {
		return (token instanceof Rule && ((Rule)token).getScope() == ScopeDatatype.PUBLIC);
	}

	@Override
	public List<Object> getMatches(Object token) {
		if (token instanceof JAXBElement) token = ((JAXBElement)token).getValue();
		if (token instanceof Rule) {
			return filter(((Rule)token).getContent());
		} else if (token instanceof Item) {
			return filter(((Item)token).getContent());
		} else if (token instanceof OneOf) {
			return filter(((OneOf)token).getItem());
		} else {
			return new ArrayList<Object>();
		}
	}

	@Override
	public boolean isOneOf(Object token) {
		if (token instanceof JAXBElement) token = ((JAXBElement)token).getValue();
		return token instanceof OneOf;
	}

	@Override
	public boolean isRuleRef(Object token) {
		if (token instanceof JAXBElement) 
			token = ((JAXBElement)token).getValue();
		return (token instanceof Ruleref && (((Ruleref)token).getUri() != null || ((Ruleref)token).getSpecial() != null));
	}

	@Override
	public boolean isItem(Object token) {
		if (token instanceof JAXBElement) token = ((JAXBElement)token).getValue();
		return token instanceof Item;
	}

	@Override
	public int getMinRepeat(Object token) {
		if (token instanceof JAXBElement) token = ((JAXBElement)token).getValue();
		if (token instanceof Item) {
			String repeat = ((Item)token).getRepeat();
			if (repeat != null) {
				if (repeat.contains("-"))
					return Integer.parseInt(repeat.split("-")[0]);
				else
					return Integer.parseInt(repeat);
			}
		}
		return 1;
	}
	
	@Override
	public int getMaxRepeat(Object token) {
		if (token instanceof JAXBElement) token = ((JAXBElement)token).getValue();
		if (token instanceof Item) {
			String repeat = ((Item)token).getRepeat();
			if (repeat != null) {
				String max;
				if (repeat.contains("-"))
					max = repeat.split("-")[1].trim();
				else
					max = repeat.trim();
				if (max.length() == 0)
					return INFINITY;
				else
					return Integer.parseInt(max);
			}
		}
		return 1;
	}

	@Override
	public String getRuleRef(Object token) {
		if (token instanceof JAXBElement) token = ((JAXBElement)token).getValue();
		if (token instanceof Ruleref) {
			Ruleref ruleref = (Ruleref)token;
			if (ruleref.getUri() != null) 
				return ruleref.getUri().substring(1);
			else if (ruleref.getSpecial() != null)
				return ruleref.getSpecial().name();
			else
				return null;
		} else {
			return null;
		}
	}

	@Override
	public boolean isWord(Object token) {
		return token instanceof String;
	}

	@Override
	public boolean isTag(Object token) {
		if (token instanceof JAXBElement) {
			JAXBElement elem = (JAXBElement) token;
			return (elem.getName().getLocalPart().equals("tag"));
		} else return false;
	}
	
	@Override
	public String getWordString(Object word) {
		return word.toString();
	}

	@Override
	public String getTagScript(Object matcher) {
		JAXBElement elem = (JAXBElement) matcher;
		return elem.getValue().toString().trim();
	}

	@Override
	public Language getLanguage() {
		return new Language(grammar.getLang());
	}

	@Override
	public String getRoot() {
		if (grammar.getRoot() != null)
			return ((Rule)grammar.getRoot()).getId();
		else
			return null;
	}
	
	@Override
	public void marshal(OutputStream out) {
		try {
			srgsMarshaller.marshal(srgsFactory.createGrammar(grammar), out);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Float getWeight(Object matcher) {
		if (matcher instanceof Item) {
			return Converters.asFloat(((Item)matcher).getWeight());
		} else {
			return null;
		}
	}
	
	@Override
	public String toString() {
		try {
			return srgsMarshaller.marshal(srgsFactory.createGrammar(grammar));
		} catch (JAXBException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void validate(String srgs) throws SAXException, IOException {
		XmlUtils.validate(srgs, xmlXsd, srgsXsd);
	}
}
