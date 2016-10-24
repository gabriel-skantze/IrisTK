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
package iristk.app.chess;

/**
 * Class containing Hash Table for a particular game.
 */
public class HashTable {
	public static final int NONULL = 04;
	public static final int EXACT = 03;
	public static final int UPPER = 02;
	public static final int LOWER = 01;
	public static final int WORTHLESS = 00;

	private long[] word1;
	private long[] word2;
	private long[] worda1;
	private long[] worda2;
	private int old = 0, old1 = 0, age = 0;
	private int hits, stores, probes, collisions, hash_size;

	private long[][] hashval = new long[12][64];

	public HashTable(int sz) {
		RandomX r = new RandomX();
		int i, j;

		if (sz > 0) {
			word1 = new long[sz];
			word2 = new long[sz];
			worda1 = new long[sz * 2];
			worda2 = new long[sz * 2];
		}
		hash_size = sz;

		for (i = 0; i < 12; ++i)
			for (j = 0; j < 64; ++j)
				hashval[i][j] = r.nextLong();

		StatReset();

	}

	/*
	 * age 3 07 depth 7 0177 type 2 03 val 21 07777777 pvc 23 037777777
	 */

	public int[] probe(long hkey, int ply, int depth, int side, int alpha,
			int beta, int random) {
		long w1;
		long w2;
		int key;
		int[] ret = new int[4];
		boolean found = false;

		ret[0] = alpha;
		ret[1] = beta;
		ret[2] = WORTHLESS;
		ret[3] = 0;

		if (hash_size == 0)
			return ret;

		probes++;

		w2 = (side == 1 ? hkey : ~hkey);
		key = (int) (w2 & (hash_size - 1));
		w1 = word1[key];
		if (w2 == word2[key])
			ret[3] = (int) (w1 >> 33 & 037777777);
		int d = (int) (w1 >> 3 & 0177);
		if (depth <= d && w2 == word2[key]) {
			found = true;
			w1 = (w1 & (~07L)) | ((age & 07));
			word1[key] = w1;
		} else {
			key = (int) (w2 & (hash_size * 2 - 1));
			w1 = worda1[key];
			if (w2 == worda2[key])
				ret[3] = (int) (w1 >> 33 & 037777777);
			d = (int) (w1 >> 3 & 0177);
			if (depth <= d && w2 == worda2[key]) {
				found = true;
				w1 = (w1 & (~07L)) | ((age & 07));
				worda1[key] = w1;
			}
		}

		if (found) {
			int val = (int) (w1 >> 12 & 07777777) - Board.WIN_SCORE;
			int type = (int) (w1 >> 10 & 03);

			hits++;

			switch (type) {
			case EXACT:
				if (val > Board.WIN_SCORE - 100)
					val -= (ply - 1);
				else if (val < -Board.WIN_SCORE + 100)
					val += (ply - 1);
				else
					val += random * side;
				ret[0] = val;
				ret[2] = EXACT;
				break;
			case UPPER:
				val += random * side;
				if (val <= alpha) {
					ret[0] = val;
					ret[2] = UPPER;
				} else
					ret[2] = NONULL;
				break;
			case LOWER:
				val += random * side;
				if (val >= beta) {
					ret[1] = val;
					ret[2] = LOWER;
				} else
					ret[2] = NONULL;
				break;
			}

		}

		return ret;
	}

	public void store(long hkey, int pvc, int ply, int depth, int side,
			int type, int val, int random) {
		if (hash_size == 0)
			return;

		long w1, w2 = (side == 1 ? hkey : ~hkey);
		int key = (int) (w2 & (hash_size - 1));
		w1 = word1[key];

		if (depth < 0)
			depth = 0;
		int a = (int) (w1 & 07);
		int d = (int) (w1 >> 3 & 0177);

		if (type == EXACT) {
			if (val > Board.WIN_SCORE - 100)
				val = val + ply - 1;
			else if (val < -Board.WIN_SCORE + 100)
				val = val - ply + 1;
			else
				val -= random * side;
		} else
			val -= random * side;
		w1 = pvc & 037777777;
		w1 = (w1 << 21) | ((long) (val + Board.WIN_SCORE) & 07777777);
		w1 = (w1 << 2) | ((type & 03));
		w1 = (w1 << 7) | ((depth & 0177));
		w1 = (w1 << 3) | ((age & 07));
		stores++;

		if (expired(a) || depth >= d) {
			if (word2[key] != w2 && word2[key] != 0) {
				int k = (int) (word2[key] & (hash_size * 2 - 1));
				worda1[k] = word1[key];
				worda2[k] = word2[key];
			}
			if (word2[key] != 0 && word2[key] != w2 && age == a)
				collisions++;
			word1[key] = w1;
			word2[key] = w2;
		} else {
			key = (int) (w2 & (hash_size * 2 - 1));
			if (worda2[key] != 0 && worda2[key] != w2
					&& age == (int) (worda1[key] & 07))
				collisions++;
			worda1[key] = w1;
			worda2[key] = w2;
		}
	}

	public boolean expired(int a) {
		// (a != age && a != old && a != old1)
		return (a != age);
	}

	public void aged() {
		old1 = old;
		old = age;
		age = (age + 1) & 07;
	}

	public long hashVal(long hkey, int piece, int row, int col) {
		int i = (piece < 0 ? piece + 6 : piece + 5);
		int j = row << 3 | col;
		if (piece == 0)
			return hkey;
		return hashval[i][j] ^ hkey;
	}

	public long hashVal(Board b) {
		long hkey = 0;
		int i, j;
		for (i = 0; i < 8; ++i)
			for (j = 0; j < 8; ++j)
				hkey = hashVal(hkey, b.getSpace(i, j), i, j);

		return hkey;
	}

	public void StatReset() {
		hits = 0;
		stores = 0;
		probes = 0;
		collisions = 0;
	}

	public String StatString() {
		if (hash_size == 0)
			return "";
		return " hs=" + stores + " hp=" + probes + " hh=" + hits + " " + 100
				* hits / (probes + 1) + "% " + " hc=" + collisions + " " + 100
				* collisions / (stores + 1) + "% ";
	}

	public boolean isSet() {
		return (hash_size > 0);
	}
}
