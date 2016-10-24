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
 * Utility class for keeping up with a move and it's score.
 * 
 * This class is not encapsulated.
 */
public class Move {
	/**
	 * Dummy instance of Move used for type checking in the defined method.
	 */
	private static Move dummy_inst = new Move();

	/**
	 * Score for this move.
	 */
	public int score;

	// Next move in the linked list of moves.
	Move next;

	// Source location for this move.
	// changed June 16, 1999 by RL
	// int from_row, from_col;
	private int f_row, f_col;

	// Destination location for this move.
	// changed June 16, 1999 by RL
	// int to_row, to_col;
	private int t_row, t_col;

	// Added June 16, 1999 by RL
	private int p_piece, p_captured, p_promote;

	private int p_piece_id;

	// Added June 18, 1999 by RL
	private int m_pvc;

	private boolean ep_flag = false;

	// Added June 16, 1999 by RL
	public Move() {
		f_row = 0;
		f_col = 0;
		t_row = 0;
		t_col = 0;
		p_piece = 0;
		p_piece_id = 0;
		p_captured = 0;
		p_promote = 0;
		m_pvc = 0;
	}

	// Added June 16, 1999 by RL
	public Move(Board b, int from_r, int from_c, int to_r, int to_c) {
		f_row = from_r;
		f_col = from_c;
		t_row = to_r;
		t_col = to_c;
		p_piece = b.getSpace(f_row, f_col);
		p_piece_id = b.getPieceId(f_row, f_col);
		if (castle())
			p_captured = Board.EMPTY;
		else
			p_captured = b.getSpace(t_row, t_col);
		p_promote = Board.EMPTY;
		if ((p_piece == Board.R_PAWN || p_piece == Board.B_PAWN)
				&& p_captured == Board.EMPTY
				&& b.getEPsq() == ((t_row << 3) | t_col)) {
			p_captured = -p_piece;
			ep_flag = true;
		}
		m_pvc = 0;
	}

	// Added June 17, 1999 by RL
	// for promotion moves
	public Move(Board b, int from_r, int from_c, int to_r, int to_c, int promote) {
		f_row = from_r;
		f_col = from_c;
		t_row = to_r;
		t_col = to_c;
		p_piece = b.getSpace(f_row, f_col);
		p_piece_id = b.getPieceId(f_row, f_col);
		if (castle())
			p_captured = 0;
		else
			p_captured = b.getSpace(t_row, t_col);
		p_promote = promote;
		m_pvc = 0;
		ep_flag = false;
	}

	// Added June 17, 1999 by RL
	// for PV moves
	public Move(int pvc) {
		int side, bit;

		bit = pvc;
		p_promote = bit & 07;
		bit >>= 3;

		p_captured = bit & 07;
		bit >>= 3;

		p_piece = bit & 07;
		bit >>= 3;

		t_col = bit & 07;
		bit >>= 3;

		t_row = bit & 07;
		bit >>= 3;

		f_col = bit & 07;
		bit >>= 3;

		f_row = bit & 07;
		bit >>= 3;

		int ep = bit & 01;
		bit >>= 1;
		if (ep == 1)
			ep_flag = true;
		else
			ep_flag = false;

		side = bit & 01;
		if (side == 1) {
			p_piece *= -1;
			p_promote *= -1;
		} else
			p_captured *= -1;
		m_pvc = pvc;
	}

	/**
	 * Return true if this move is defined (i.e. if it is a class derived from
	 * Move rather than an instance of Move itself.
	 */
	public boolean defined() {
		return getClass() != dummy_inst.getClass();
	}

	/**
	 * Modify the given board to reflect making this move.
	 * 
	 * Classes derived from Move can override this function to make more
	 * interesting moves.
	 */
	public void apply(Board b) {
	}

	/**
	 * Modify the given board to return it to the state before this move was
	 * made.
	 * 
	 * Classes derived from Move can override this function to make more
	 * interesting moves.
	 */
	public void undo(Board b) {
	}

	// Added June 16, 1999 by RL
	/**
	 * Return the captured piece.
	 */
	public int captured() {
		if (p_captured != 0 && castle())
			return 0;
		return p_captured;
	}

	// Added June 16, 1999 by RL
	/**
	 * Return the promoted piece.
	 */
	public int promote() {
		return p_promote;
	}

	// Added June 16, 1999 by RL
	/**
	 * Return the moved piece.
	 */
	public int piece() {
		return p_piece;
	}

	public int piece_id() {
		return p_piece_id;
	}

	// Added June 16, 1999 by RL
	int from_row() {
		return f_row;
	}

	int from_col() {
		return f_col;
	}

	int to_row() {
		return t_row;
	}

	int to_col() {
		return t_col;
	}

	// Added June 17, 1999 by RL
	/**
	 * Return the true if moves are the same.
	 */
	public boolean isEqual(Move m) {
		return f_row == m.f_row && f_col == m.f_col && t_row == m.t_row
				&& t_col == m.t_col && p_piece == m.p_piece
				&& p_captured == m.p_captured && p_promote == m.p_promote
				&& ep_flag == m.ep_flag;
	}

	// Added June 17, 1999 by RL
	/**
	 * Make a copy a Move.
	 */
	public void copy(Move m) {
		f_row = m.f_row;
		f_col = m.f_col;
		t_row = m.t_row;
		t_col = m.t_col;
		p_piece = m.p_piece;
		p_piece_id = m.p_piece_id;
		p_captured = m.p_captured;
		p_promote = m.p_promote;
		ep_flag = m.ep_flag;
	}

	// Added June 17, 1999 by RL
	/**
	 * Make a string presentation of a Move.
	 */
	public String string() {
		char[] p = { 'K', 'Q', 'B', 'N', 'R', 'P', ' ', 'P', 'R', 'N', 'B',
				'Q', 'K' };
		char[] c = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h' };

		if (p_piece == 0)
			return "null";
		if (castle()) {
			if (t_col < f_col)
				return "O-O-O";
			else
				return "O-O";
		}

		String s = "";
		s = s + p[p_piece + 6];
		s = s + c[f_col] + (8 - f_row);
		if (p_captured != 0) {
			s = s + "x" + p[p_captured + 6];
		} else
			s = s + "-";
		s = s + c[t_col] + (8 - t_row);
		if (p_promote != 0) {
			s = s + "=";
			s = s + p[p_promote + 6];
		}
		if (ep_flag)
			s = s + " e.p.";

		return s;
	}

	// Added June 17, 1999 by RL
	private boolean castle() {
		return ((p_piece == Board.B_KING || p_piece == Board.R_KING) && (t_col > f_col + 1 || t_col < f_col - 1));
	}

	// Added June 17, 1999 by RL
	public void display() {
		System.out.println("Move ==> " + string());
	}

	// Added June 17, 1999 by RL
	// for PV moves
	public int pvCode() {
		int side, bit, ep;

		if (m_pvc != 0)
			return m_pvc;

		bit = 0;
		if (p_piece < 0)
			side = 1;
		else
			side = 0;
		bit |= side;

		bit <<= 1;
		if (ep_flag)
			ep = 1;
		else
			ep = 0;
		bit |= ep;

		bit <<= 3;
		bit |= f_row;

		bit <<= 3;
		bit |= f_col;

		bit <<= 3;
		bit |= t_row;

		bit <<= 3;
		bit |= t_col;

		bit <<= 3;
		if (p_piece < 0)
			bit |= (p_piece * -1);
		else
			bit |= p_piece;

		bit <<= 3;
		if (p_captured < 0)
			bit |= (p_captured * -1);
		else
			bit |= p_captured;

		bit <<= 3;
		if (p_promote < 0)
			bit |= (p_promote * -1);
		else
			bit |= p_promote;

		m_pvc = bit;

		return bit;

	}

	// Added June 18, 1999 by RL
	// for moves history
	public int index() {
		int bit;

		bit = f_row;

		bit <<= 3;
		bit |= f_col;

		bit <<= 3;
		bit |= t_row;

		bit <<= 3;
		bit |= t_col;

		return bit;
	}

	public Position getFrom() {
		return new Position(from_row(), from_col());
	}

	public Position getTo() {
		return new Position(to_row(), to_col());
	}

}
