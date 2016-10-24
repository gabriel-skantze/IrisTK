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

import iristk.util.RandomList.RandomModel;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class RandomMap {
	
	private HashMap<Integer,RandomList> deck = new HashMap<Integer, RandomList>();
	static Random randomGenerator = new Random();
	
	public int next(int index, int n) {
		return next(index, n, RandomModel.DECK_RESHUFFLE_NOREPEAT);
	}
	
	public int next(int index, int n, RandomModel model) {
		if (!deck.containsKey(index)) {
			deck.put(index, new RandomList(n, model));
		}
		return deck.get(index).next();
	}
	
	public Object next(List<?> list) {
		if (list.size() == 0)
			throw new IllegalArgumentException("Cannot choose from empty list");
		else if (list.size() == 1)
			return list.get(0);
		else
			return list.get(next(list.hashCode(), list.size()));
	}
	
	public Object next(List<?> list, RandomModel model) {
		if (list.size() == 0)
			throw new IllegalArgumentException("Cannot choose from empty list");
		else if (list.size() == 1)
			return list.get(0);
		else
			return list.get(next(list.hashCode(), list.size(), model));
	}
	
	public float getFloat() {
		return randomGenerator.nextFloat();
	}

	public int getInt(int i) {
		return randomGenerator.nextInt(i);
	}
	
 	
}
