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

import java.io.*;
import java.net.URI;
import java.util.*;

public class ListGrammar extends GrammarModel {

	public ListGrammar(URI uri, Language lang, String ruleId) throws IOException {
		this(uri.toURL().openStream(), lang, ruleId);
	}
	
	public ListGrammar(File file, Language lang, String ruleId) throws IOException {
		this(new FileInputStream(file), lang, ruleId);
	}
	
	public ListGrammar(InputStream is, Language lang, String ruleId) throws IOException {
		setLanguage(lang);
		setRoot(ruleId);
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String line;
		ArrayList<Object> matches = new ArrayList<>();
		OneOf oneof = new OneOf();
		matches.add(oneof);
		while ((line = br.readLine()) != null) {
			line = line.trim(); 
			if (line.length() > 0) {
				String[] cols = line.split(" +");
				if (cols.length > 1) {
					oneof.add(new Item(Arrays.asList(cols)));
				} else {
					oneof.add(line);
				}
			}
		}
		br.close();
		addRule(ruleId, true, matches);
	}
	
	
	
	@Override
	public void marshal(OutputStream out) {
		
	}

}
