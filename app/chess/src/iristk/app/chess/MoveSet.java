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
import java.util.HashSet;

public class MoveSet {

	private final ArrayList<Move> moves;

	public MoveSet(Move move) {
		moves = new ArrayList<Move>();
		for (Move m = move; m != null; m = m.next) {
			moves.add(m);
		}
	}

	public int size() {
		return moves.size();
	}

	public int sizeFrom() {
		HashSet<Integer> from = new HashSet<Integer>();
		for (Move m : moves) {
			from.add(m.from_col() * 10 + m.from_row());
		}
		return from.size();
	}

	public Move get(int i) {
		return moves.get(i);
	}

	public PositionSet getFrom() {
		HashSet<Integer> from = new HashSet<Integer>();
		PositionSet positions = new PositionSet();
		for (Move m : moves) {
			int key = m.from_col() * 10 + m.from_row();
			if (!from.contains(key)) {
				from.add(key);
				positions.add(new Position(m.from_row(), m.from_col()));
			}
		}
		return positions;
	}

	public PositionSet getTo() {
		PositionSet positions = new PositionSet();
		for (Move m : moves) {
			positions.add(new Position(m.to_row(), m.to_col()));
		}
		return positions;
	}

	public boolean contains(int fromRow, int fromCol, int toRow, int toCol) {
		for (Move m : moves) {
			if (m.from_col() == fromCol && m.from_row() == fromRow
					&& m.to_col() == toCol && m.to_row() == toRow)
				return true;
		}
		return false;
	}

	public boolean containsFrom(int fromRow, int fromCol) {
		for (Move m : moves) {
			if (m.from_col() == fromCol && m.from_row() == fromRow)
				return true;
		}
		return false;
	}

	public boolean containsTo(int toRow, int toCol) {
		for (Move m : moves) {
			if (m.to_col() == toCol && m.to_row() == toRow)
				return true;
		}
		return false;
	}

	public void constrainFrom(PositionSet positionSet) {
		int i = 0;
		while (i < moves.size()) {
			Move m = moves.get(i);
			if (positionSet.contains(m.from_row(), m.from_col())) {
				i++;
			} else {
				moves.remove(i);
			}
		}
	}

	public void constrainTo(PositionSet positionSet) {
		int i = 0;
		while (i < moves.size()) {
			Move m = moves.get(i);
			if (positionSet.contains(m.to_row(), m.to_col())) {
				i++;
			} else {
				moves.remove(i);
			}
		}
	}

	public void constrainDistance(int distance) {
		int i = 0;
		while (i < moves.size()) {
			Move m = moves.get(i);
			if (calcDistance(m) == distance) {
				i++;
			} else {
				moves.remove(i);
			}
		}
	}

	public void constrainDirection(String direction) {
		int i = 0;
		while (i < moves.size()) {
			Move m = moves.get(i);
			if ((direction.equals("Left") && m.to_col() < m.from_col())
					|| (direction.equals("Right") && m.to_col() > m.from_col())
					|| (direction.equals("Backward") && m.to_row() > m
							.from_row())
					|| (direction.equals("Forward") && m.to_row() < m
							.from_row())) {
				i++;
			} else {
				moves.remove(i);
			}
		}
	}

	public static int calcDistance(Move m) {
		return calcDistance(m.from_col(), m.from_row(), m.to_col(), m.to_row());
	}

	public static int calcDistance(int from_col, int from_row, int to_col,
			int to_row) {
		return Math.max(Math.abs(from_col - to_col),
				Math.abs(from_row - to_row));
	}

}
