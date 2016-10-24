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
import java.util.List;
import java.util.Random;

public class RandomList {
	
	public enum RandomModel {
		// The numbers are randomized in a deck. The order of the deck is preserved across iterations. 
		DECK (true),
		// The numbers are randomized in a deck. The deck is reshuffled after each iterations.
		DECK_RESHUFFLE (true),
		// The numbers are randomized in a deck. The deck is reshuffled after each iterations, but only so that no two consecutive number are identical.
		DECK_RESHUFFLE_NOREPEAT (true),
		// The numbers are completely random, without concern for previous numbers.
		DICE (false),
		// The numbers are completely random, but no two consecutive number are identical.
		DICE_NOREPEAT (false),
		// The numbers are retrieved in order.
		NONRANDOM (true);
		
		RandomModel(boolean sequence) {
			this.sequence = sequence;
		}
		
		public final boolean sequence;
	}
	
	private int position;
	private ArrayList<Integer> sequence;
	private RandomModel model; 
	static Random randomGenerator = new Random();
	private int n;
	private int lastNumber = -1;
	
	public RandomList(int n) {
		this(n, RandomModel.DECK_RESHUFFLE_NOREPEAT);
	}
	
	public RandomList(int n, RandomModel model) {
		this.model = model;
		this.n = n;
		if (model.sequence) {
			position = 0;
			sequence = new ArrayList<Integer>(n);
			for (int i = 0; i < n; i++) {
				sequence.add(i);
			}
			if (model != RandomModel.NONRANDOM)
				shuffle(sequence);
		}
	}
	
	public int next() {
		if (model == RandomModel.DICE) {
			lastNumber = randomGenerator.nextInt(n);
		} else if (model == RandomModel.DICE_NOREPEAT) {
			int res;
			do {
				res = randomGenerator.nextInt(n);
			} while (n > 1 && res == lastNumber);
			lastNumber = res;
		} else {
			if (position >= sequence.size()) {
				position = 0;
				if (model == RandomModel.DECK_RESHUFFLE) {
					shuffle(sequence);
				} else if (model == RandomModel.DECK_RESHUFFLE_NOREPEAT) {
					do {
						shuffle(sequence);
					} while (n > 1 && sequence.get(0) == lastNumber);
				}
			}
			lastNumber = sequence.get(position);
			position++;
		}
		return lastNumber;
	}
	
	// Fisher-Yates shuffle algorithm
	public static void shuffle(List list) {
		for (int i = list.size() - 1; i >= 1; i--) {
			int j = randomGenerator.nextInt(i + 1);
			Object x = list.get(i);
			list.set(i, list.get(j));
			list.set(j, x);
		}
	}
	
	public static Object getRandom(List list) {
		return list.get(randomGenerator.nextInt(list.size()));
	}
	
	public static int nextInt(int i) {
		return randomGenerator.nextInt(i);
	}
	
	public static void main(String[] args) {
		RandomList list = new RandomList(5);
		for (int i = 0; i < 100; i++) {
			System.out.println(list.next());
		}
	}
	
}
