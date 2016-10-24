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
 * A static board evaluator for Chess
 */
public class ChessEvaluator extends SBE {

	// boards with bonus scores for positional score:
	// *****************************************
	static int KnightCenterScores[] = { -4, 0, 0, 0, 0, 0, 0, -4, -4, 0, 2, 2,
			2, 2, 0, -4, -4, 2, 3, 2, 2, 3, 2, -4, -4, 2, 2, 5, 5, 2, 2, -4,
			-4, 2, 2, 5, 5, 2, 2, -4, -4, 2, 3, 2, 2, 3, 2, -4, -4, 0, 2, 2, 2,
			2, 0, -4, -4, 0, 0, 0, 0, 0, 0, -4 };

	static int PawnCenterScores[] = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 4, 0, 0, 0, 0, 0, 5, 7,
			7, 5, 0, 0, 0, 0, 3, 4, 4, 3, 0, 0, 0, 0, -1, -2, -2, -1, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0 };

	static int PawnColScores[] = { -30, -20, -10, 0, 0, -10, -20, -30 };

	static int PawnRowScores[] = { 0, 0, 5, 7, 9, 11, 15, 0 };

	static int KingCenterScores[] = { 0, 0, 1, 1, 1, 1, 0, 0, 0, 1, 2, 3, 3, 2,
			1, 0, 1, 2, 6, 7, 7, 6, 2, 1, 1, 3, 7, 9, 9, 7, 3, 1, 1, 3, 7, 9,
			9, 7, 3, 1, 1, 2, 6, 7, 7, 6, 2, 1, 0, 1, 2, 3, 3, 2, 1, 0, 0, 0,
			1, 1, 1, 1, 0, 0 };

	static int QueenCenterScores[] = { -2, 0, 0, 0, 0, 0, 0, -2, -2, 0, 0, 0,
			0, 0, 0, -2, -2, 0, 1, 1, 1, 1, 0, -2, -2, 0, 1, 2, 2, 1, 0, -2,
			-2, 0, 1, 2, 2, 1, 0, -2, -2, 0, 1, 1, 1, 1, 0, -2, -2, 0, 0, 0, 0,
			0, 0, -2, -2, 0, 0, 0, 0, 0, 0, -2 };

	/**
	 * Use the constructor as a chance to initialize constants or arrays used in
	 * the evaluator.
	 */
	public ChessEvaluator() {
	}

	/**
	 * Decide who is doing better in board b and return an appropariate score.
	 * Higher values if B_SIDE is winning, lower if R_SIDE is winning.
	 */
	@Override
	public int evaluate(Board b) {
		int score = 0;

		int red_counter = 0;
		int blue_counter = 0;
		boolean red_king = false;
		boolean blue_king = false;

		for (int row = 0; row < Board.HEIGHT; row++)
			for (int col = 0; col < Board.WIDTH; col++) {
				if (b.getSpace(row, col) < 0)
					red_counter += MaterialScore(b, row, col)
							+ PositionalScore(b, row, col);
				else
					blue_counter += MaterialScore(b, row, col)
							+ PositionalScore(b, row, col);
				if (b.getSpace(row, col) == Board.R_KING)
					red_king = true;
				if (b.getSpace(row, col) == Board.B_KING)
					blue_king = true;
			}

		score = blue_counter - red_counter;

		if (!blue_king)
			score = Board.R_WIN_SCORE;
		if (!red_king)
			score = Board.B_WIN_SCORE;

		return score;
	}

	private int PositionalScore(Board b, int row, int col) {

		switch (b.getSpace(row, col)) {
		case Board.R_QUEEN:
			return QueenCenterScores[(row * 8 + col)];

		case Board.B_QUEEN:
			return QueenCenterScores[(row * 8 + col)];

		case Board.R_PAWN:
			return PawnCenterScores[(row * 8 + col)] + PawnColScores[col]
					+ PawnRowScores[7 - row];

		case Board.B_PAWN:
			return PawnCenterScores[63 - (row * 8 + col)] + PawnColScores[col]
					+ PawnRowScores[row];

		case Board.R_KNIGHT:
			return KnightCenterScores[(row * 8 + col)];

		case Board.B_KNIGHT:
			return KnightCenterScores[(row * 8 + col)];

		case Board.R_KING:
			return KingCenterScores[(row * 8 + col)];

		case Board.B_KING:
			return KingCenterScores[(row * 8 + col)];

		}
		return 0;

	}

	private int MaterialScore(Board b, int row, int col) {
		return Board.VAL[b.getSpace(row, col) + 6];
	}

}
