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
 * Class containing Principal Variation for a particular search.
 */
public class PV {

	private int[][] path = new int[Board.MAX_PLY + 1][Board.MAX_PLY + 1];
	private boolean[][] end = new boolean[Board.MAX_PLY + 1][Board.MAX_PLY + 1];

	public PV() {
		end[0][0] = true;
	}

	public void add(Move m, int ply) {
		int i = ply;

		while (i < Board.MAX_PLY && !end[ply][i] && path[ply][i] != 0) {
			path[ply - 1][i] = path[ply][i];
			end[ply - 1][i] = false;
			++i;
		}
		end[ply - 1][i] = true;
		end[ply - 1][ply - 1] = false;
		path[ply - 1][ply - 1] = m.pvCode();
	}

	public void append(Move m, int ply) {
		end[ply - 1][ply] = true;
		end[ply - 1][ply - 1] = false;
		path[ply - 1][ply - 1] = m.pvCode();
	}

	public void set(int ply) {
		path[ply - 1][ply - 1] = 0;
		end[ply - 1][ply - 1] = true;
	}

	public boolean check(Move m, int ply) {
		return (path[0][ply - 1] == m.pvCode());
	}

	public String display() {
		String s = "";
		Move m;
		for (int i = 0; !end[0][i]; ++i) {
			m = new Move(path[0][i]);
			if (i > 0 && i % 7 == 0)
				s = s + "\n          ";
			s = s + " " + m.string();
		}
		return s;
	}

	public int Hint() {
		return path[0][1];
	}

	public void Copy(PV p) {
		int i;
		for (i = 0; !p.end[0][i]; ++i) {
			path[0][i] = p.path[0][i];
			end[0][i] = false;
		}
		end[0][i] = true;
	}

	public void Next() {
		int i, j = 0;
		for (i = 0; !end[0][i] && i <= 1; ++i)
			;
		if (i > 1)
			for (j = 0; !end[0][i]; ++i) {
				path[0][j] = path[0][i];
				end[0][j] = false;
				++j;
			}
		end[0][j] = true;
	}
}
