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

import java.io.OutputStream;
import java.util.List;

public interface Grammar {

	public int INFINITY = Integer.MAX_VALUE;
		
	List<Object> getRules();
	
	String getRuleId(Object rule);
	
	boolean isPublic(Object rule);

	List<Object> getMatches(Object ruleOrMatcher);

	boolean isOneOf(Object matcher);

	boolean isRuleRef(Object matcher);
	
	boolean isItem(Object matcher);

	int getMinRepeat(Object matcher);
	
	int getMaxRepeat(Object matcher);

	String getRuleRef(Object matcher);

	boolean isWord(Object matcher);
	
	String getWordString(Object word);

	boolean isTag(Object matcher);
	
	String getTagScript(Object matcher);
	
	Language getLanguage();

	String getRoot();
	
	Float getWeight(Object matcher);
	
	void marshal(OutputStream out);
	
}
