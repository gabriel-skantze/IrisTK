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

import java.util.ArrayList;
import java.util.List;

public class WordEdge extends Edge {

	private Word word;
	
	public WordEdge(int i, Word word) {
		this.word = word;
		this.begin = i;
		this.end = i + 1;
	}
	
	@Override
	public boolean isPassive() {
		return true;
	}

	@Override
	public List<Edge> matches(Edge passive) {
		return null;
	}

	public Word getWord() {
		return word;
	}
	
	@Override
	public String toString() {
		return "WordEdge{" + word.toString() + ":" + begin + ":" + end + "}";
	}

	@Override
	public List<Word> getWords() {
		ArrayList<Word> words = new ArrayList<Word>();
		words.add(word);
		return words;
	}

	@Override
	public int getLevel() {
		return 0;
	}

	@Override
	public boolean isActive() {
		return false;
	}
}
