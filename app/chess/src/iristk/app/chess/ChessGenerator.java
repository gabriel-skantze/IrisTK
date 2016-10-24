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
/*
 * Copyright 2013-2014, Gabriel Skantze. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU General Public License for more details.
 */
package iristk.app.chess;

/**
 * A simple move that just moves a piece from a source location to a destination
 * location, possibly with a capture.
 */
class SimpleMove extends Move {
	// Piece that was previously at the destination.
	private int saved_piece;
	private int saved_piece_id;
	private int epsq;

	// Save the contents of the move flag for the source and destination
	// positions.
	boolean to_flag, from_flag;

	/**
	 * Construct a move that moves the piece from f_row, f_col to t_row, t_col
	 */
	SimpleMove(Board b, int f_row, int f_col, int t_row, int t_col) {
		super(b, f_row, f_col, t_row, t_col);
	}

	/**
	 * Move a piece from the from position to the to position
	 */
	@Override
	public void apply(Board b) {
		// Make a copy of the target piece.

		saved_piece = b.getSpace(to_row(), to_col());
		saved_piece_id = b.getPieceId(to_row(), to_col());
		epsq = b.getEPsq();
		b.setEPsq(0);
		if (piece() == Board.B_PAWN || piece() == Board.R_PAWN) {
			if (from_row() + 2 == to_row())
				b.setEPsq(((from_row() + 1) << 3) | from_col());
			else if (from_row() - 2 == to_row())
				b.setEPsq(((from_row() - 1) << 3) | from_col());
		}

		// Make copies of the source and destination move flags.
		to_flag = b.getFlag(to_row(), to_col());
		from_flag = b.getFlag(from_row(), from_col());

		// Flag these two spaces as having been moved.
		b.setFlag(to_row(), to_col(), true);
		b.setFlag(from_row(), from_col(), true);

		// Make a copy of the piece in the to position
		b.setSpace(to_row(), to_col(), b.getSpace(from_row(), from_col()),
				b.getPieceId(from_row(), from_col()));

		// Empty the from position
		b.setSpace(from_row(), from_col(), Board.EMPTY, Board.EMPTY);

		// Added June 17, 1999 by RL
		if (!b.thinking())
			display();

		// Added June 23, 1999 by RL
		b.GameHistory(pvCode());
	}

	/**
	 * Erase the mark from the space at row/col.
	 */
	@Override
	public void undo(Board b) {
		// Added June 23, 1999 by RL
		b.GameHistory(-1);

		// Restore the source and destination move flags
		b.setFlag(to_row(), to_col(), to_flag);
		b.setFlag(from_row(), from_col(), from_flag);

		// Put a copy of the piece in the back in the from position
		b.setSpace(from_row(), from_col(), b.getSpace(to_row(), to_col()),
				b.getPieceId(to_row(), to_col()));

		// Empty the to position
		b.setSpace(to_row(), to_col(), saved_piece, saved_piece_id);

		b.setEPsq(epsq);
	}
}

// change June 17, 1999 by RL
// changed to handle promotion to other pieces
/**
 * A special move that moves a pawn and promotes it to a queen at the same time.
 * This code assumes that the pawn is stored as a value of 1.
 */
class PromoteMove extends Move {
	// Piece that was previously at the destination.
	private int saved_piece;
	private int saved_piece_id;
	private int epsq;

	// Save the contents of the move flag for the source and destination
	// positions.
	boolean to_flag, from_flag;

	/**
	 * Construct a move that moves the piece from f_row, f_col to t_row, t_col
	 */
	PromoteMove(Board b, int f_row, int f_col, int t_row, int t_col) {
		super(b, f_row, f_col, t_row, t_col, b.getSpace(f_row, f_col)
				* Board.B_QUEEN);
	}

	// Added June 17, 1999 by RL
	/**
	 * Construct a move that moves the piece from f_row, f_col to t_row, t_col
	 * and promoted to a p_promote
	 */
	PromoteMove(Board b, int f_row, int f_col, int t_row, int t_col,
			int p_promote) {
		super(b, f_row, f_col, t_row, t_col, p_promote);
	}

	/**
	 * Move a piece from the from position to the to position
	 */
	@Override
	public void apply(Board b) {
		// Make a copy of the saved piece.
		saved_piece = b.getSpace(to_row(), to_col());
		saved_piece_id = b.getPieceId(to_row(), to_col());

		epsq = b.getEPsq();
		b.setEPsq(0);

		// Make copies of the source and destination move flags.
		to_flag = b.getFlag(to_row(), to_col());
		from_flag = b.getFlag(from_row(), from_col());

		// Flag these two spaces as having been moved.
		b.setFlag(to_row(), to_col(), true);
		b.setFlag(from_row(), from_col(), true);

		// Make a copy of the piece in the to position
		b.setSpace(to_row(), to_col(), promote(),
				b.getPieceId(from_row(), from_col()));

		// Empty the from position
		b.setSpace(from_row(), from_col(), Board.EMPTY, Board.EMPTY);

		// Added June 17, 1999 by RL
		if (!b.thinking())
			display();

		// Added June 23, 1999 by RL
		b.GameHistory(pvCode());
	}

	/**
	 * Erase the mark from the space at row/col.
	 */
	@Override
	public void undo(Board b) {
		// Added June 23, 1999 by RL
		b.GameHistory(-1);

		// Put a copy of the piece in the back in the from position
		b.setSpace(from_row(), from_col(), piece(), piece_id());

		// Restore the source and destination move flags
		b.setFlag(to_row(), to_col(), to_flag);
		b.setFlag(from_row(), from_col(), from_flag);

		// Empty the to position
		b.setSpace(to_row(), to_col(), saved_piece, saved_piece_id);

		b.setEPsq(epsq);
	}

}

/**
 * A CastleMove performs the castle operation between a king and the rook at the
 * other end of the move.
 */
class CastleMove extends Move {
	int epsq;

	/**
	 * Construct a move that moves the piece from f_row, f_col to t_row, t_col
	 */
	CastleMove(Board b, int f_row, int f_col, int t_row, int t_col) {
		super(b, f_row, f_col, t_row, t_col);
	}

	/**
	 * Move a piece from the from position to the to position
	 */
	@Override
	public void apply(Board b) {
		epsq = b.getEPsq();
		b.setEPsq(0);

		if (to_col() < from_col()) {
			b.setSpace(to_row(), from_col() - 2,
					b.getSpace(from_row(), from_col()),
					b.getPieceId(from_row(), from_col()));
			b.setSpace(to_row(), from_col() - 1, b.getSpace(to_row(), 0),
					b.getPieceId(to_row(), 0));

			// Original position of rook made empty.
			b.setSpace(to_row(), 0, Board.EMPTY, Board.EMPTY);

			// Flag rook space as having been moved.
			b.setFlag(to_row(), 0, true);
		} else {
			b.setSpace(to_row(), from_col() + 2,
					b.getSpace(from_row(), from_col()),
					b.getPieceId(from_row(), from_col()));
			b.setSpace(to_row(), from_col() + 1,
					b.getSpace(to_row(), Board.WIDTH - 1),
					b.getPieceId(to_row(), Board.WIDTH - 1));

			// Original position of rook made empty.
			b.setSpace(to_row(), Board.WIDTH - 1, Board.EMPTY, Board.EMPTY);

			// Flag rook space as having been moved.
			b.setFlag(to_row(), Board.WIDTH - 1, true);
		}

		// Original position king made empty.
		b.setSpace(from_row(), from_col(), Board.EMPTY, Board.EMPTY);

		// Flag king space as having been moved.
		b.setFlag(from_row(), from_col(), true);

		// Added June 17, 1999 by RL
		if (!b.thinking())
			display();

		// Added June 23, 1999 by RL
		b.GameHistory(pvCode());
	}

	/**
	 * Erase the mark from the space at row/col.
	 */
	@Override
	public void undo(Board b) {
		// Added June 23, 1999 by RL
		b.GameHistory(-1);

		// Restore the positions of the king and the rook.
		if (to_col() < from_col()) {
			b.setSpace(to_row(), 0, b.getSpace(to_row(), from_col() - 1),
					b.getPieceId(to_row(), from_col() - 1));
			b.setSpace(from_row(), from_col(),
					b.getSpace(to_row(), from_col() - 2),
					b.getPieceId(to_row(), from_col() - 2));

			b.setSpace(to_row(), from_col() - 2, Board.EMPTY, Board.EMPTY);
			b.setSpace(to_row(), from_col() - 1, Board.EMPTY, Board.EMPTY);

			// Restore the move flags for rook space
			b.setFlag(to_row(), 0, false);
		} else {
			b.setSpace(to_row(), Board.WIDTH - 1,
					b.getSpace(to_row(), from_col() + 1),
					b.getPieceId(to_row(), from_col() + 1));
			b.setSpace(from_row(), from_col(),
					b.getSpace(to_row(), from_col() + 2),
					b.getPieceId(to_row(), from_col() + 2));

			b.setSpace(to_row(), from_col() + 2, Board.EMPTY, Board.EMPTY);
			b.setSpace(to_row(), from_col() + 1, Board.EMPTY, Board.EMPTY);

			// Restore the move flags for rook space
			b.setFlag(to_row(), Board.WIDTH - 1, false);
		}

		// Restore the move flags for king space
		b.setFlag(from_row(), from_col(), false);

		b.setEPsq(epsq);
	}

	// Added June 16, 1999 by RL
	/**
	 * Return the captured piece.
	 */
	@Override
	public int captured() {
		return 0;
	}

	// Added June 17, 1999 by RL
	/**
	 * Make a string presentation of a Move.
	 */
	@Override
	public String string() {
		if (to_col() < from_col())
			return "O-O-O";
		else
			return "O-O";
	}
}

/**
 * A EPMove performs the en passant capture of pawn.
 */
class EPMove extends Move {
	int saved_piece;
	int saved_piece_id;
	int epsq;
	boolean from_flag, to_flag;

	/**
	 * Construct a move that moves the piece from f_row, f_col to t_row, t_col
	 */
	EPMove(Board b, int f_row, int f_col, int t_row, int t_col) {
		super(b, f_row, f_col, t_row, t_col);
		epsq = b.getEPsq();
	}

	/**
	 * Move a piece from the from position to the to position
	 */
	@Override
	public void apply(Board b) {
		epsq = b.getEPsq();
		b.setEPsq(0);

		saved_piece = b.getSpace(from_row(), epsq & 07);
		saved_piece_id = b.getPieceId(from_row(), epsq & 07);
		b.setSpace(from_row(), epsq & 07, Board.EMPTY, Board.EMPTY);
		b.setSpace(to_row(), to_col(), piece(), piece_id());
		b.setSpace(from_row(), from_col(), Board.EMPTY, Board.EMPTY);

		// Flag these two spaces as having been moved.
		from_flag = b.getFlag(from_row(), from_col());
		to_flag = b.getFlag(to_row(), to_col());
		b.setFlag(from_row(), from_col(), true);
		b.setFlag(to_row(), to_col(), true);

		// Added June 17, 1999 by RL
		if (!b.thinking())
			display();

		// Added June 23, 1999 by RL
		b.GameHistory(pvCode());
	}

	/**
	 * Erase the mark from the space at row/col.
	 */
	@Override
	public void undo(Board b) {
		// Added June 23, 1999 by RL
		b.GameHistory(-1);

		b.setSpace(from_row(), from_col(), piece(), piece_id());
		b.setSpace(to_row(), to_col(), Board.EMPTY, Board.EMPTY);
		b.setSpace(from_row(), epsq & 07, saved_piece, saved_piece_id);

		// Restore the move flags for these two spaces
		b.setFlag(from_row(), from_col(), from_flag);
		b.setFlag(to_row(), to_col(), to_flag);

		b.setEPsq(epsq);
	}

}

/**
 * A fancy move generator that knows how to move every piece in chess.
 */
public class ChessGenerator extends Generator {

	// Generate all possible moves for the checker at position
	// row, col. Add these moves to the front of move_list and return
	// the result.
	private Move movePawn(Move move_list, Board b, int side, int row, int col) {
		// Pawns always move in the same direction, fortunately,
		// the side constant gives us the right direction.
		if (row + side >= 0 && row + side < Board.HEIGHT) {
			if (b.getSpace(row + side, col) == Board.EMPTY) {
				Move m;

				// See if we get to promote the pawn
				if (row + side == 0 || row + side == Board.HEIGHT - 1)
					m = new PromoteMove(b, row, col, row + side, col);
				else
					m = new SimpleMove(b, row, col, row + side, col);
				m.next = move_list;
				move_list = m;

				// See if this is the pawn's first move or some later move.
				if ((side == Board.B_SIDE && row == 1)
						|| (side == Board.R_SIDE && row == 6))
					if (b.getSpace(row + side * 2, col) == Board.EMPTY) {
						// Record the new move and link it into the move_list
						m = new SimpleMove(b, row, col, row + side * 2, col);
						m.next = move_list;
						move_list = m;
					}
			}

			// Consider capture moves
			// First, to the left.
			if (col > 0 && b.getSpace(row + side, col - 1) * side < 0) {
				Move m;
				// See if we get to promote the pawn
				if (row + side == 0 || row + side == Board.HEIGHT - 1)
					m = new PromoteMove(b, row, col, row + side, col - 1);
				else
					m = new SimpleMove(b, row, col, row + side, col - 1);
				m.next = move_list;
				move_list = m;
			}

			// Then to the right
			if (col + 1 < Board.WIDTH
					&& b.getSpace(row + side, col + 1) * side < 0) {
				// Record the new move and link it into the move_list
				Move m;
				// See if we get to promote the pawn
				if (row + side == 0 || row + side == Board.HEIGHT - 1)
					m = new PromoteMove(b, row, col, row + side, col + 1);
				else
					m = new SimpleMove(b, row, col, row + side, col + 1);
				m.next = move_list;
				move_list = m;
			}

			// e.p capture
			int epsq = b.getEPsq();
			if (epsq > 0 && col + 1 < Board.WIDTH
					&& epsq == (((row + side) << 3) | (col + 1))) {
				Move m = new EPMove(b, row, col, row + side, col + 1);
				m.next = move_list;
				move_list = m;
			} else if (epsq > 0 && col > 0
					&& epsq == (((row + side) << 3) | (col - 1))) {
				Move m = new EPMove(b, row, col, row + side, col - 1);
				m.next = move_list;
				move_list = m;
			}

		}
		return move_list;
	}

	private Move moveKnight(Move move_list, Board b, int side, int row, int col) {
		if (row > 0) {
			// See if the destination space is empty or has an enemy piece.
			if (col - 2 >= 0 && b.getSpace(row - 1, col - 2) * side <= 0) {
				Move m = new SimpleMove(b, row, col, row - 1, col - 2);
				m.next = move_list;
				move_list = m;
			}
			// See if the destination space is empty or has an enemy piece.
			if (col + 2 < Board.WIDTH
					&& b.getSpace(row - 1, col + 2) * side <= 0) {
				Move m = new SimpleMove(b, row, col, row - 1, col + 2);
				m.next = move_list;
				move_list = m;
			}

			if (row - 1 > 0) {
				// See if the destination space is empty or has an enemy piece.
				if (col > 0 && b.getSpace(row - 2, col - 1) * side <= 0) {
					Move m = new SimpleMove(b, row, col, row - 2, col - 1);
					m.next = move_list;
					move_list = m;
				}
				// See if the destination space is empty or has an enemy piece.
				if (col + 1 < Board.WIDTH
						&& b.getSpace(row - 2, col + 1) * side <= 0) {
					Move m = new SimpleMove(b, row, col, row - 2, col + 1);
					m.next = move_list;
					move_list = m;
				}
			}
		}

		if (row + 1 < Board.HEIGHT) {
			// See if the destination space is empty or has an enemy piece.
			if (col - 2 >= 0 && b.getSpace(row + 1, col - 2) * side <= 0) {
				Move m = new SimpleMove(b, row, col, row + 1, col - 2);
				m.next = move_list;
				move_list = m;
			}
			// See if the destination space is empty or has an enemy piece.
			if (col + 2 < Board.WIDTH
					&& b.getSpace(row + 1, col + 2) * side <= 0) {
				Move m = new SimpleMove(b, row, col, row + 1, col + 2);
				m.next = move_list;
				move_list = m;
			}

			if (row + 2 < Board.HEIGHT) {
				// See if the destination space is empty or has an enemy piece.
				if (col > 0 && b.getSpace(row + 2, col - 1) * side <= 0) {
					Move m = new SimpleMove(b, row, col, row + 2, col - 1);
					m.next = move_list;
					move_list = m;
				}
				// See if the destination space is empty or has an enemy piece.
				if (col + 1 < Board.WIDTH
						&& b.getSpace(row + 2, col + 1) * side <= 0) {
					Move m = new SimpleMove(b, row, col, row + 2, col + 1);
					m.next = move_list;
					move_list = m;
				}
			}
		}

		return move_list;
	}

	// Generate the moves for the king. This is a little
	// tricky since the king can castle.
	private Move moveKing(Move move_list, Board b, int side, int row, int col) {
		// Move straight up or diagonal
		if (row > 0) {
			if (b.getSpace(row - 1, col) * side <= 0) {
				Move m = new SimpleMove(b, row, col, row - 1, col);
				m.next = move_list;
				move_list = m;
			}

			if (col > 0 && b.getSpace(row - 1, col - 1) * side <= 0) {
				Move m = new SimpleMove(b, row, col, row - 1, col - 1);
				m.next = move_list;
				move_list = m;
			}

			if (col + 1 < Board.WIDTH
					&& b.getSpace(row - 1, col + 1) * side <= 0) {
				Move m = new SimpleMove(b, row, col, row - 1, col + 1);
				m.next = move_list;
				move_list = m;
			}
		}

		// Move straight down or diagonal
		if (row + 1 < Board.HEIGHT) {
			if (b.getSpace(row + 1, col) * side <= 0) {
				Move m = new SimpleMove(b, row, col, row + 1, col);
				m.next = move_list;
				move_list = m;
			}

			if (col > 0 && b.getSpace(row + 1, col - 1) * side <= 0) {
				Move m = new SimpleMove(b, row, col, row + 1, col - 1);
				m.next = move_list;
				move_list = m;
			}

			if (col + 1 < Board.WIDTH
					&& b.getSpace(row + 1, col + 1) * side <= 0) {
				Move m = new SimpleMove(b, row, col, row + 1, col + 1);
				m.next = move_list;
				move_list = m;
			}
		}

		// Move left or right
		if (col > 0 && b.getSpace(row, col - 1) * side <= 0) {
			Move m = new SimpleMove(b, row, col, row, col - 1);
			m.next = move_list;
			move_list = m;
		}

		if (col + 1 < Board.WIDTH && b.getSpace(row, col + 1) * side <= 0) {
			Move m = new SimpleMove(b, row, col, row, col + 1);
			m.next = move_list;
			move_list = m;
		}

		// Detect legal castle moves.
		if (b.getFlag(row, col) == false) {
			// See if we can castle to the left
			if (b.getFlag(row, 0) == false) {
				// Make sure the path between the king and the rook is vacant.
				boolean vacant = true;
				for (int i = col - 1; i != 0; i--)
					if (b.getSpace(row, i) != Board.EMPTY)
						vacant = false;

				if (vacant) {
					// Now, the really expensive check. Make sure none of the
					// spaces on the way is threatened.
					for (int i = col; i != col - 3 && vacant; i--)
						if (threatened(b, side, row, i))
							vacant = false;

					if (vacant) {
						// Move m = new CastleMove( b, row, col, row, 0 );
						Move m = new CastleMove(b, row, col, row, col - 2);
						m.next = move_list;
						move_list = m;
					}
				}
			}

			// See if we can castle to the right
			if (b.getFlag(row, Board.WIDTH - 1) == false) {
				// Make sure the path between the king and the rook is vacant.
				boolean vacant = true;
				for (int i = col + 1; i != Board.WIDTH - 1; i++)
					if (b.getSpace(row, i) != Board.EMPTY)
						vacant = false;

				if (vacant) {
					// Now, the really expensive check. Make sure none of the
					// spaces on the way is threatened.
					for (int i = col; i != col + 3 && vacant; i++)
						if (threatened(b, side, row, i))
							vacant = false;

					if (vacant) {
						// Move m = new CastleMove( b, row, col, row,
						// Board.WIDTH - 1 );
						Move m = new CastleMove(b, row, col, row, col + 2);
						m.next = move_list;
						move_list = m;
					}
				}
			}
		}

		return move_list;
	}

	// Utility function for pieces that can move diagonally.
	// Concatenate moves onto the given move_list.
	protected Move moveDiagonal(Move move_list, Board b, int side, int row,
			int col) {
		// left-up
		boolean NoPiece = true;
		int tmpcol = -1;
		int tmprow = -1;
		while (col + tmpcol >= 0 && row + tmprow >= 0 && NoPiece) {
			if (b.getSpace(row + tmprow, col + tmpcol) == 0) {
				Move m = new SimpleMove(b, row, col, row + tmprow, col + tmpcol);
				m.next = move_list;
				move_list = m;
				tmpcol--;
				tmprow--;
			} else {
				if (b.getSpace(row + tmprow, col + tmpcol) * side < 0) {
					Move m = new SimpleMove(b, row, col, row + tmprow, col
							+ tmpcol);
					m.next = move_list;
					move_list = m;
				}
				NoPiece = false;
			}
		}

		// right-up
		NoPiece = true;
		tmpcol = 1;
		tmprow = -1;
		while (col + tmpcol <= 7 && row + tmprow >= 0 && NoPiece) {
			if (b.getSpace(row + tmprow, col + tmpcol) == 0) {
				Move m = new SimpleMove(b, row, col, row + tmprow, col + tmpcol);
				m.next = move_list;
				move_list = m;
				tmpcol++;
				tmprow--;
			} else {
				if (b.getSpace(row + tmprow, col + tmpcol) * side < 0) {
					Move m = new SimpleMove(b, row, col, row + tmprow, col
							+ tmpcol);
					m.next = move_list;
					move_list = m;
				}
				NoPiece = false;
			}
		}

		// right-down
		NoPiece = true;
		tmpcol = 1;
		tmprow = 1;
		while (col + tmpcol <= 7 && row + tmprow <= 7 && NoPiece) {
			if (b.getSpace(row + tmprow, col + tmpcol) == 0) {
				Move m = new SimpleMove(b, row, col, row + tmprow, col + tmpcol);
				m.next = move_list;
				move_list = m;
				tmpcol++;
				tmprow++;
			} else {
				if (b.getSpace(row + tmprow, col + tmpcol) * side < 0) {
					Move m = new SimpleMove(b, row, col, row + tmprow, col
							+ tmpcol);
					m.next = move_list;
					move_list = m;
				}
				NoPiece = false;
			}
		}

		// left-down
		NoPiece = true;
		tmpcol = -1;
		tmprow = 1;
		while (col + tmpcol >= 0 && row + tmprow <= 7 && NoPiece) {
			if (b.getSpace(row + tmprow, col + tmpcol) == 0) {
				Move m = new SimpleMove(b, row, col, row + tmprow, col + tmpcol);
				m.next = move_list;
				move_list = m;
				tmpcol--;
				tmprow++;
			} else {
				if (b.getSpace(row + tmprow, col + tmpcol) * side < 0) {
					Move m = new SimpleMove(b, row, col, row + tmprow, col
							+ tmpcol);
					m.next = move_list;
					move_list = m;
				}
				NoPiece = false;
			}
		}

		return move_list;
	}

	// Utility function for pieces that move in rows or columns
	// Insert moves into move_list
	protected Move moveRectangular(Move move_list, Board b, int side, int row,
			int col) {
		boolean NoPiece = true;
		// left
		int tmpcol = -1;
		while (col + tmpcol >= 0 && NoPiece) {
			if (b.getSpace(row, col + tmpcol) == 0) {
				Move m = new SimpleMove(b, row, col, row, col + tmpcol);
				m.next = move_list;
				move_list = m;
				tmpcol--;
			} else {
				if (b.getSpace(row, col + tmpcol) * side < 0) {
					Move m = new SimpleMove(b, row, col, row, col + tmpcol);
					m.next = move_list;
					move_list = m;
				}
				NoPiece = false;
			}

		}

		// up
		NoPiece = true;
		int tmprow = -1;
		while (row + tmprow >= 0 && NoPiece) {
			if (b.getSpace(row + tmprow, col) == 0) {
				Move m = new SimpleMove(b, row, col, row + tmprow, col);
				m.next = move_list;
				move_list = m;
				tmprow--;
			} else {
				if (b.getSpace(row + tmprow, col) * side < 0) {
					Move m = new SimpleMove(b, row, col, row + tmprow, col);
					m.next = move_list;
					move_list = m;
				}
				NoPiece = false;
			}

		}

		// right
		NoPiece = true;
		tmpcol = 1;
		while (col + tmpcol <= 7 && NoPiece) {
			if (b.getSpace(row, col + tmpcol) == 0) {
				Move m = new SimpleMove(b, row, col, row, col + tmpcol);
				m.next = move_list;
				move_list = m;
				tmpcol++;
			} else {
				if (b.getSpace(row, col + tmpcol) * side < 0) {
					Move m = new SimpleMove(b, row, col, row, col + tmpcol);
					m.next = move_list;
					move_list = m;
				}
				NoPiece = false;
			}
		}

		// down
		NoPiece = true;
		tmprow = 1;
		while (row + tmprow <= 7 && NoPiece) {
			if (b.getSpace(row + tmprow, col) == 0) {
				Move m = new SimpleMove(b, row, col, row + tmprow, col);
				m.next = move_list;
				move_list = m;
				tmprow++;
			} else {
				if (b.getSpace(row + tmprow, col) * side < 0) {
					Move m = new SimpleMove(b, row, col, row + tmprow, col);
					m.next = move_list;
					move_list = m;
				}
				NoPiece = false;
			}

		}

		return move_list;
	}

	// Return true if the given side is threatened (i.e. could a
	// piece at that space be captured. This function is a bit
	// of a hack, but it should work for all of the standard pieces
	// in chess.
	private boolean threatened(Board b, int side, int row, int col) {
		Move move_list;
		Move m;

		// This tries each possible threat backward from the row, col
		// space. Then it checks to see if there is the right kind
		// of piece at the to position to make that move back to the row, col
		// space.
		move_list = movePawn(null, b, side, row, col);
		for (m = move_list; m != null; m = m.next)
			if (b.getSpace(m.to_row(), m.to_col()) * side == Board.R_PAWN)
				return true;

		move_list = moveRectangular(null, b, side, row, col);
		for (m = move_list; m != null; m = m.next)
			if (b.getSpace(m.to_row(), m.to_col()) * side == Board.R_ROOK
					|| b.getSpace(m.to_row(), m.to_col()) * side == Board.R_QUEEN)
				return true;

		move_list = moveDiagonal(null, b, side, row, col);
		for (m = move_list; m != null; m = m.next)
			if (b.getSpace(m.to_row(), m.to_col()) * side == Board.R_BISHOP
					|| b.getSpace(m.to_row(), m.to_col()) * side == Board.R_QUEEN)
				return true;

		move_list = moveKnight(null, b, side, row, col);
		for (m = move_list; m != null; m = m.next)
			if (b.getSpace(m.to_row(), m.to_col()) * side == Board.R_KNIGHT)
				return true;

		// Temporarily set the moved flag so we don't try to castle from
		// inside this function
		boolean saved_flag = b.getFlag(row, col);
		b.setFlag(row, col, true);

		move_list = moveKing(null, b, side, row, col);
		for (m = move_list; m != null; m = m.next)
			if (b.getSpace(m.to_row(), m.to_col()) * side == Board.R_KING) {
				b.setFlag(row, col, saved_flag);
				return true;
			}

		b.setFlag(row, col, saved_flag);

		return false;
	}

	@Override
	public Move generateMoves(Board b, int side) {
		// Start out with an empty move list.
		Move move_list = null;

		// Check every space on the board.
		for (int row = 0; row < Board.HEIGHT; row++)
			for (int col = 0; col < Board.WIDTH; col++) {

				// See if this space contains a pawn for the given side.
				if (b.getSpace(row, col) == Board.B_PAWN * side)
					move_list = movePawn(move_list, b, side, row, col);

				// See if this space contains a rook for the given side.
				if (b.getSpace(row, col) == Board.B_ROOK * side)
					move_list = moveRectangular(move_list, b, side, row, col);

				if (b.getSpace(row, col) == Board.B_KNIGHT * side)
					move_list = moveKnight(move_list, b, side, row, col);

				if (b.getSpace(row, col) == Board.B_BISHOP * side)
					move_list = moveDiagonal(move_list, b, side, row, col);

				if (b.getSpace(row, col) == Board.B_QUEEN * side) {
					// Queens move like rooks and like bishops
					move_list = moveDiagonal(move_list, b, side, row, col);
					move_list = moveRectangular(move_list, b, side, row, col);
				}

				if (b.getSpace(row, col) == Board.B_KING * side)
					move_list = moveKing(move_list, b, side, row, col);
			}

		return move_list;
	}

}
