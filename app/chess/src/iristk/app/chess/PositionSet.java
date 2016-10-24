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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class PositionSet extends ArrayList<Position> {

	public static int UP = 1;
	public static int DOWN = 2;
	public static int LEFT = 3;
	public static int RIGHT = 4;

	public boolean contains(int row, int column) {
		for (Position p : this) {
			if (p.getCol() == column && p.getRow() == row)
				return true;
		}
		return false;
	}

	public void constrainRelPiecePos(String rel, PositionSet positionSet) {
		int i = 0;
		Outer: while (i < size()) {
			Position p = get(i);
			for (Position prel : positionSet) {
				if ((rel.equals("LeftOf") && p.getCol() == prel.getCol() - 1 && p
						.getRow() == prel.getRow())
						|| (rel.equals("RightOf")
								&& p.getCol() == prel.getCol() + 1 && p
								.getRow() == prel.getRow())
						|| (rel.equals("Behind") && p.getCol() == prel.getCol() && p
								.getRow() == prel.getRow() + 1)
						|| (rel.equals("FrontOf")
								&& p.getCol() == prel.getCol() && p.getRow() == prel
								.getRow() - 1)
						|| (rel.equals("NextTo")
								&& Math.abs(p.getCol() - prel.getCol()) <= 1 && Math
								.abs(p.getRow() - prel.getRow()) <= 1)) {
					i++;
					continue Outer;
				}
			}
			remove(i);
		}
	}

	public void constrainRelPos(String rel) {
		int i = 0;
		Outer: while (i < size()) {
			Position p = get(i);
			for (Position prel : this) {
				if ((rel.equals("Left") && p.getCol() > prel.getCol())
						|| (rel.equals("Right") && p.getCol() < prel.getCol())
						|| (rel.equals("Back") && p.getRow() < prel.getRow())
						|| (rel.equals("Front") && p.getRow() > prel.getRow())) {
					remove(i);
					continue Outer;
				}
			}
			i++;
		}
	}

	public void constrainSquare(String column, int row) {
		int i = 0;
		while (i < size()) {
			Position p = get(i);
			if (compSquare(column, row, p.getCol(), p.getRow())) {
				i++;
			} else {
				remove(i);
			}
		}
	}

	public boolean hasDistinctDistances(Position origin) {
		HashSet<Integer> distances = new HashSet<Integer>();
		for (Position p : this) {
			Integer distance = MoveSet.calcDistance(origin.getCol(),
					origin.getRow(), p.getCol(), p.getRow());
			if (distances.contains(distance)) {
				return false;
			}
			distances.add(distance);
		}
		return true;
	}

	public HashMap<Integer, Integer> getDirections(Position origin) {
		HashMap<Integer, Integer> directions = new HashMap<Integer, Integer>();
		directions.put(LEFT, 0);
		directions.put(RIGHT, 0);
		directions.put(UP, 0);
		directions.put(DOWN, 0);
		for (Position p : this) {
			if (origin.getCol() > p.getCol()) {
				directions.put(LEFT, directions.get(LEFT) + 1);
			}
			if (origin.getCol() < p.getCol()) {
				directions.put(RIGHT, directions.get(RIGHT) + 1);
			}
			if (origin.getRow() > p.getRow()) {
				directions.put(UP, directions.get(UP) + 1);
			}
			if (origin.getRow() < p.getRow()) {
				directions.put(DOWN, directions.get(DOWN) + 1);
			}
		}
		return directions;
	}

	private static final String[] columnLabels = new String[] { "A", "B", "C",
			"D", "E", "F", "G", "H" };

	public static boolean compSquare(String descColumn, int descRow,
			int column, int row) {
		return (8 - descRow) == row && columnLabels[column].equals(descColumn);
	}

	public void constrain(PositionSet ps) {
		int i = 0;
		Outer: while (i < size()) {
			Position p = get(i);
			if (!ps.contains(p.getRow(), p.getCol())) {
				remove(i);
				continue Outer;
			}
			i++;
		}
	}

}
