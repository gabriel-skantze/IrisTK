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
package iristk.speech.nuance9;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VocabTool {

	public static void main(String[] args) {
		HashSet<String> rulerefs = new HashSet<String>();
		HashSet<String> vocab = new HashSet<String>();
		for (String file : args) {
			try {
				BufferedReader br = new BufferedReader(new FileReader(file));
				String line;
				StringBuilder sb = new StringBuilder();
				while ((line = br.readLine()) != null) {
					if (line.matches(".*<sentence.*")) {
						line = line.replaceAll("<sentence.*?>", "");
						line = line.replaceAll("</sentence>.*", "");
						Matcher m = Pattern.compile("<ruleref.*?>").matcher(line);
						while (m.find()) {
							rulerefs.add(m.group());
						}
						line = line.replaceAll("<ruleref.*?>", "");
						line = line.trim();
						for (String word : line.split(" ")) {
							if (word.length() > 0) {
								vocab.add("<item>" + word + "</item>");
							}
						}
					}
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			for (String word : vocab) {
				System.out.println(word);
			}
			for (String word : rulerefs) {
				System.out.println(word);
			}
		}
	}
	
}
