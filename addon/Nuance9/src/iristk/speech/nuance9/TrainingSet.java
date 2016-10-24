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

import java.io.*;
import java.util.*;

public class TrainingSet {

	HashSet<String> words = new HashSet<String>();
	ArrayList<String> sentences = new ArrayList<String>();

	public TrainingSet(File file) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader (file));
		String line;
		while ((line = br.readLine()) != null) {
			line = line.trim().replaceAll("  +", " ");
			line = line.replaceAll("<ruleref +uri", "<ruleref_uri");
			if (line.length() > 0) {
				for (String word : line.split(" ")) {
					words.add(word.replaceAll("_", " "));
				}
				sentences.add(line.replaceAll("_", " "));
			}
		}
	}

	public void toXML(OutputStream out) {
		PrintStream ps = new PrintStream(out);
		ps.println("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
		ps.println("<SLMTraining version=\"1.0.0\" xml:lang=\"en-us\">");
		ps.println("<param name=\"ngram_order\"><value>3</value></param>");
		ps.println("<vocab>");
		ArrayList<String> wlist = new ArrayList<String>(words);
		Collections.sort(wlist);
		for (String word : wlist) {
			if (word.contains("<"))
				ps.println(word);
			else
				ps.println("<item>" + word + "</item>");
		}
		ps.println("</vocab>");
		ps.println("<training>");
		for (String sentence : sentences) {
			ps.println("<sentence>" + sentence + "</sentence>");
		}
		ps.println("</training>");
		ps.println("</SLMTraining>");
	}
	
	public static void main(String[] args) {
		try {
			TrainingSet set = new TrainingSet(new File("c:/dropbox/KTH/Exjobb/Carl/corpus_ns_classes.txt"));
			FileOutputStream out = new FileOutputStream(new File("c:/dropbox/KTH/Exjobb/Carl/corpus_ns_classes.xml"));
			set.toXML(out);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void train(File file) {
		try {
			Runtime.getRuntime().exec("sgc -train " + file.getAbsolutePath());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
