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
 * A class representing a board for playing games with X and O (like
 * tic-tac-toe). An instance of Board remembers the state of a single board and
 * supports methods for making moves on the board as well as selecting commputer
 * moves.
 */
public class Board {
	// /////////////////////////////////////////////////////////////
	// Constants for board state

	// Size of the board
	public static final int WIDTH = 8;
	public static final int HEIGHT = 8;

	// Constants for the 2 different sides
	public static final int R_SIDE = -1;
	public static final int B_SIDE = 1;

	
	public static final int PAWN = 1;
	public static final int ROOK = 2;
	public static final int KNIGHT = 3;
	public static final int BISHOP = 4;
	public static final int QUEEN = 5;
	public static final int KING = 6;

	// Constants for the 7 different symbols
	public static final int EMPTY = 0;
	public static final int R_PAWN = -PAWN;
	public static final int B_PAWN = PAWN;
	public static final int R_ROOK = -ROOK;
	public static final int B_ROOK = ROOK;
	public static final int R_KNIGHT = -KNIGHT;
	public static final int B_KNIGHT = KNIGHT;
	public static final int R_BISHOP = -BISHOP;
	public static final int B_BISHOP = BISHOP;
	public static final int R_QUEEN = -QUEEN;
	public static final int B_QUEEN = QUEEN;
	public static final int R_KING = -KING;
	public static final int B_KING = KING;

	
	public static final int DRAW = 2;

	
	public static final int PAWN_VAL = 100;
	public static final int ROOK_VAL = 500;
	public static final int KNIGHT_VAL = 300;
	public static final int BISHOP_VAL = 330;
	public static final int QUEEN_VAL = 900;
	public static final int KING_VAL = 50000;

	public static final int[] PIECES = { R_PAWN, B_PAWN, R_ROOK, B_ROOK,
			R_KNIGHT, B_KNIGHT, R_BISHOP, B_BISHOP, R_QUEEN, B_QUEEN, R_KING,
			B_KING };

	
	public static final int[] VAL = { KING_VAL, QUEEN_VAL, BISHOP_VAL,
			KNIGHT_VAL, ROOK_VAL, PAWN_VAL, EMPTY, PAWN_VAL, ROOK_VAL,
			KNIGHT_VAL, BISHOP_VAL, QUEEN_VAL, KING_VAL };

	
	public static final int[] VAL_NEG = { -KING_VAL, -QUEEN_VAL, -BISHOP_VAL,
			-KNIGHT_VAL, -ROOK_VAL, -PAWN_VAL, EMPTY, PAWN_VAL, ROOK_VAL,
			KNIGHT_VAL, BISHOP_VAL, QUEEN_VAL, KING_VAL };

	// /////////////////////////////////////////////////////////////
	// Constants for move generation

	// Time limit for the computer to make a move.
	// give it 5 seconds.
	
	// private static final int TIME_LIMIT = 5000;
	// private static final int TIME_LIMIT = 15000;
	private static int timeLimit = 15000;

	
	public static final int MAX_PLY = 65;

	// Winning scores for a move
	public static final int WIN_SCORE = 1000000;
	public static final int B_WIN_SCORE = WIN_SCORE;
	public static final int R_WIN_SCORE = -WIN_SCORE;
	
	private static final boolean DEBUG = false;

	// Move generator for this game.
	private Generator generator;

	// Static Board Evaluator for this game.
	private SBE sbe;

	// Flag that gets set to true when computer uses up all of its time
	// to make a move.
	boolean timeout_flag;

	// Time at which the computer started choosing its move.
	long search_start_time;

	// Counter used to check some things infrequently during search (elapsed
	// time for example).
	int lazy_counter = 0;

	// Search node counter to measure pruning performance.
	private int search_counter;

	// Two-dimensional array for the contents of the board.
	private int[][] space = new int[HEIGHT][WIDTH];

	// Two-dimensional array for the piece id:s.
	private int[][] pieceId = new int[HEIGHT][WIDTH];

	// For chess, we need to keep up with whether or not the king or the
	// rooks have moved. This will let us tell if we can castle or
	// not. The mflag for each space is true if that space has been
	// moved into or out of.

	
	// public boolean[][] mflag = new boolean[ HEIGHT ][ WIDTH ];
	private boolean[][] mflag = new boolean[HEIGHT][WIDTH];

	
	private int max_depth;

	
	private volatile boolean end_search;
	static private volatile boolean stopped = false;

	
	private PV pv;
	private boolean thinking_flag;
	private Move[] current_move = new Move[MAX_PLY + 1];

	private Statistics stat;

	private int[] move_history = new int[4096];
	private int[] killer_move1 = new int[MAX_PLY + 1],
			killer_move2 = new int[MAX_PLY + 1],
			killer_move3 = new int[MAX_PLY + 1],
			killer_move4 = new int[MAX_PLY + 1];
	// private boolean[] check = new boolean[MAX_PLY+1];

	
	private long hashKey;
	static private HashTable hashTable;

	
	private int[] history_move = new int[200];
	private long[] history_hash = new long[200];
	private int history_count;

	private int hint = 0, ep_sq = 0;
	private boolean pondering = false, puzzling = false, predicted = false;
	private boolean root_first_move, fail_low, fail_high;

	private int random_score;
	private RandomX rnd;
	private boolean hc = false;

	private Move lastMove;
	public GameListener gameListener = null;

	/**
	 * Make an empty board object.
	 */
	public Board(Generator gen, SBE sbe, HashTable ht) {
		// Remember copies of the given generator and SBE.
		generator = gen;
		this.sbe = sbe;
		hashTable = ht;

		
		pv = new PV();

		
		hashKey = 0;

		
		history_count = 0;

		stat = null;
		ep_sq = 0;
		rnd = new RandomX();

		lastMove = null;
	}

	/**
	 * Fill in the board with a starting configuration
	 */
	public void startingConfig() {
		int row, col;

		
		hashKey = 0;

		// Set all of the moveflags to false.
		for (row = 0; row < HEIGHT; row++)
			for (col = 0; col < WIDTH; col++)
				mflag[row][col] = false;

		// Set the board to empty.
		for (row = 0; row < HEIGHT; row++)
			for (col = 0; col < WIDTH; col++) {
				space[row][col] = EMPTY;
			}

		// Fill in interesting pieces after that.
		space[0][0] = B_ROOK;
		space[0][1] = B_KNIGHT;
		space[0][2] = B_BISHOP;
		space[0][3] = B_QUEEN;
		space[0][4] = B_KING;
		space[0][5] = B_BISHOP;
		space[0][6] = B_KNIGHT;
		space[0][7] = B_ROOK;

		space[7][0] = R_ROOK;
		space[7][1] = R_KNIGHT;
		space[7][2] = R_BISHOP;
		space[7][3] = R_QUEEN;
		space[7][4] = R_KING;
		space[7][5] = R_BISHOP;
		space[7][6] = R_KNIGHT;
		space[7][7] = R_ROOK;

		// Put a row of pawns for each side.
		for (col = 0; col < WIDTH; col++) {
			space[1][col] = B_PAWN;
			space[6][col] = R_PAWN;
		}

		// Assign id:s to the pieces
		int id = 1;
		for (col = 0; col < WIDTH; col++) {
			for (row = 0; row < WIDTH; row++) {
				if (space[row][col] != EMPTY) {
					pieceId[row][col] = id;
					id++;
				} else {
					pieceId[row][col] = EMPTY;
				}
			}
		}

		hashKey = hashTable.hashVal(this);

		
		history_count = 0;
		history_move[0] = 0;
		history_hash[0] = hashKey;

		pv.set(1);
		hint = 0;
		pondering = false;
		puzzling = false;
		ep_sq = 0;
		lastMove = null;

		if (stat == null) {
			stat = new Statistics();
			stat.reset();
		}

	}

	/**
	 * Make a copy of this board.
	 */
	public Board Duplicate(Board b) {
		Board dup;

		if (b == null)
			dup = new Board(generator, sbe, hashTable);
		else {
			b.generator = generator;
			b.sbe = sbe;
			dup = b;
		}

		// Copy all the move flags for dup to match this
		for (int row = 0; row < HEIGHT; row++)
			for (int col = 0; col < WIDTH; col++)
				dup.mflag[row][col] = mflag[row][col];

		// Fill in all of the spaces of dup to match this
		for (int row = 0; row < HEIGHT; row++)
			for (int col = 0; col < WIDTH; col++) {
				dup.space[row][col] = space[row][col];
				dup.pieceId[row][col] = pieceId[row][col];
			}

		
		dup.hashKey = hashKey;

		
		for (int i = 0; i <= history_count; ++i) {
			dup.history_move[i] = history_move[i];
			dup.history_hash[i] = history_hash[i];
		}
		dup.history_count = history_count;

		dup.pv.Copy(pv);
		dup.hint = hint;
		dup.pondering = false;
		dup.puzzling = false;

		dup.stat = stat;
		dup.rnd = rnd;
		dup.ep_sq = ep_sq;

		dup.lastMove = lastMove;

		return dup;
	}

	/**
	 * Return the list of legal moves from the current configuration.
	 */
	public Move legalMoves(int side) {
		// Just let our move generator do the work.
		return generator.generateMoves(this, side);
	}

	
	/**
	 * Return the piece occupying a space.
	 */
	public int getSpace(int row, int col) {
		return space[row][col];
	}

	public int getPieceId(int row, int col) {
		return pieceId[row][col];
	}

	
	/**
	 * Put a piece.
	 */
	public void setSpace(int row, int col, int piece, int pieceId) {
		hashKey = hashTable.hashVal(hashKey, space[row][col], row, col);
		space[row][col] = piece;
		this.pieceId[row][col] = pieceId;
		hashKey = hashTable.hashVal(hashKey, piece, row, col);
	}

	
	/**
	 * Return the flag status of a space.
	 */
	public boolean getFlag(int row, int col) {
		return mflag[row][col];
	}

	
	/**
	 * Set the flag status of a space.
	 */
	public void setFlag(int row, int col, boolean val) {
		mflag[row][col] = val;
	}

	/**
	 * See if there is a win. Return the side (R_SIDE or B_SIDE) of the winning
	 * player.
	 */
	public int checkForWin() {
		// We can just use the SBE to check for a win.
		int score = sbe.evaluate(this);

		// See if the SBE reports a winner.
		if (score == R_WIN_SCORE)
			return R_SIDE;

		if (score == B_WIN_SCORE)
			return B_SIDE;

		
		if (GameRepeat3())
			return DRAW;

		return EMPTY;
	}

	// /////////////////////////////////////////////////////////////////////////
	// Move selection code
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * Coose a move for the computer and update the board with that move.
	 */
	public void computerMove(int side) {
		int cur_hint = 0;

		// Get a snapshot of the current time.
		search_start_time = System.currentTimeMillis();
		if (stat.start_time == 0)
			stat.start_time = search_start_time;

		// Have not yet run out of time.
		timeout_flag = false;

		
		thinking_flag = true;

		
		if (lastMove != null)
			current_move[0] = lastMove;
		else
			current_move[0] = new Move();

		long chk_hash = hashKey;

		// Move the computer is going to make, use a trivially-chosen move
		// as a starting point.
		// change June 16, 1999 by RL
		// change June 18, 1999 by RL
		// Move choice = selectMove(1, 1, side, side*WIN_SCORE, -side*WIN_SCORE
		// );
		int alpha = -50, beta = 50;
		Move choice = selectMove(1, 1, side, alpha, beta, false);
		if (choice.score <= alpha && !timeout_flag) {
			if (DEBUG)
			System.out.println("failed low: " + choice.score);
			alpha = -WIN_SCORE;
			beta = WIN_SCORE;
			fail_low = true;
			choice = selectMove(1, 1, side, alpha, beta, false);
		} else if (choice.score >= beta && !timeout_flag) {
			if (DEBUG)
			System.out.println("failed high: " + choice.score);
			alpha = -WIN_SCORE;
			beta = WIN_SCORE;
			fail_high = true;
			choice = selectMove(1, 1, side, alpha, beta, false);
		}

		// Have not yet run out of time.
		timeout_flag = false;

		
		end_search = false;

		int depth = 1;
		// Perform iterative deepening
		do {
			depth++;

			// Reset the search counter.
			search_counter = 0;

			root_first_move = false;
			fail_low = false;
			fail_high = false;

			// Let the computer select a move.
			// change June 16, 1999 by RL
			// Move new_move = selectMove(1, depth, side, side*WIN_SCORE,
			// -side*WIN_SCORE);
			Move new_move;
			alpha = choice.score - 50;
			if (alpha < -WIN_SCORE)
				alpha = -WIN_SCORE;
			beta = choice.score + 50;
			if (beta > WIN_SCORE)
				beta = WIN_SCORE;
			new_move = selectMove(1, depth, side, alpha, beta, false);
			if (new_move.score <= alpha && !timeout_flag) {
				if (DEBUG)
					System.out.println("failed low: " + new_move.score);
				alpha = -WIN_SCORE;
				beta = WIN_SCORE;
				fail_low = true;
				new_move = selectMove(1, depth, side, alpha, beta, false);
			} else if (new_move.score >= beta && !timeout_flag) {
				if (DEBUG)
					System.out.println("failed high: " + new_move.score);
				alpha = -WIN_SCORE;
				beta = WIN_SCORE;
				fail_high = true;
				new_move = selectMove(1, depth, side, alpha, beta, false);
			}

			// Report the chosen move as the computer generates it.
			if (new_move.defined() && (!timeout_flag || root_first_move)) {
				tentativeMove(new_move);
				
				
				
				// System.out.println( "Move: " + depth + " " + new_move.score +
				// " (" + new_move.from_row() + ", "
				// + new_move.from_col() + " ) -> ( " +
				// new_move.to_row() + ", " + new_move.to_col() + " )" );
				// System.out.print( "Move: " + depth + " " + new_move.score +
				// " " );
				// if (pondering && !puzzling) System.out.print("(" + new
				// Move(hint).string() + ")" );
				// System.out.println(pv.display());
				// long t = System.currentTimeMillis() - search_start_time;
				// System.out.println( "Nodes: " + search_counter + "  " +
				// t/1000 + "." + (t%1000)/100 + " secs" );
			}

			// Only take the move if we did not run out of time while selecting
			// it.
			// Otherwise, we don't really know how good it is.
			if (!timeout_flag || root_first_move)
				choice = new_move;

			if (!timeout_flag || root_first_move)
				cur_hint = pv.Hint();

			if (stopped)
				end_search = true;

			
			if (end_search && !timeout_flag)
				timeout_flag = true;

			
			if (end_search)
				if (DEBUG)
				System.out.println("Search aborted");

			// Keep searching until we run out of time or until the maixmum
			// search depth is exceeded.
		} while (!timeout_flag && depth < MAX_PLY);

		
		if (chk_hash != hashKey)
			if (DEBUG)
			System.out.println("Hash: " + chk_hash + " " + hashKey);

		
		thinking_flag = false;

		lastMove = null;

		if (puzzling) {
			if (end_search)
				return;
			if (choice.defined()) {
				hint = choice.pvCode();
				choice.apply(this);
				lastMove = choice;
			}
			return;
		}

		if (pondering)
			return;

		if (end_search)
			return;

		
		if (DEBUG)
		System.out.println("Stats:" + stat.string() + "\n      "
				+ hashTable.StatString());

		
		stat.reset();

		
		hashTable.aged();
		hashTable.StatReset();

		// Print a newline after we finally choose the move
		if (DEBUG)
		System.out.println();

		hint = cur_hint;

		if (choice.defined()) {
			// Make the chosen move
			choice.apply(this);
			lastMove = choice;
		}
	}

	private void tentativeMove(Move new_move) {
		if (gameListener != null) {
			gameListener.tentativeMove(new_move);
		}
	}

	// ---------------------------------------------------------------
	// Select the best move for the given side.
	//
	// change June 16, 1999 by RL
	// private Move selectMove(int ply, int depth, int side, int prune_thresh,
	// int pass_thresh )
	private Move selectMove(int ply, int depth, int side, int alpha, int beta,
			boolean quiesce) {
		
		int o_alpha = alpha, nscore = alpha;

		// Best move so far.
		Move best_move = new Move();

		// Assume that this state has a really low score until we know
		// that we can do better.
		// change June 16, 1999 by RL
		// change June 21, 1999 by RL
		// best_move.score = pass_thresh; //-( ( WIN_SCORE + 1 ) * side );
		best_move.score = -WIN_SCORE;

		// Count this as one move node expansion.
		search_counter++;

		
		stat.node_count++;

		// if shortest mate is already found
		if (alpha >= WIN_SCORE - (ply + 1)) {
			best_move.score = alpha;
			return best_move;
		}

		
		if (ply == 1) {
			max_depth = depth;
			root_first_move = false;
			random_score = 0;
		}

		
		if (ply > 1) {
			pv.set(ply);

			
			if (GameRepeat()) {
				best_move.score = 0;
				return best_move;
			}
		}

		// Only check the actual time every 1000 calls to this function.
		// This is an efficiency hack.
		if (--lazy_counter <= 0 && !timeout_flag) {
			lazy_counter = 1000;

			// See if we have exceeded the time limit.
			if ((!pondering || puzzling) && max_depth > 1) {
				long time_spent = System.currentTimeMillis()
						- search_start_time;
				if (time_spent > timeLimit)
					timeout_flag = true;
				else if (predicted && time_spent > 2 * timeLimit / 3)
					timeout_flag = true;
				else if (puzzling && time_spent > timeLimit / 6)
					timeout_flag = true;
				else if (root_first_move || fail_low || fail_high)
					; // nothing here
				else if (time_spent > 2 * timeLimit / 3)
					timeout_flag = true;
			}
		}

		if (stopped)
			end_search = true;

		
		if (end_search && !timeout_flag)
			timeout_flag = true;

		// Just exit out of the search when we run out of time
		if (timeout_flag)
			return best_move;

		int pv1 = 0;
		boolean donull = true;
		if (ply > 1 && !quiesce && hashTable.isSet()) {
			int[] p = hashTable.probe(hashKey, ply, depth, side, alpha, beta,
					random_score);
			int o_beta = beta;
			alpha = p[0];
			beta = p[1];
			best_move = new Move(p[3]);
			switch (p[2]) {
			case HashTable.EXACT:
				best_move.score = alpha;
				if (alpha < beta) {
					pv.append(best_move, ply);
				}
				return best_move;
			case HashTable.LOWER:
				best_move.score = beta;
				return best_move;
			case HashTable.UPPER:
				best_move.score = alpha;
				return best_move;
			case HashTable.WORTHLESS:
				pv1 = p[3];
				break;
			case HashTable.NONULL:
				pv1 = p[3];
				donull = false;
				break;
			}
			best_move.score = -WIN_SCORE;
		}

		
		// Null Move
		if (ply > 1 && depth > 1 && !quiesce && donull
				&& current_move[ply - 1].piece() != 0
				&& current_move[ply - 2].piece() != 0) {
			current_move[ply] = new Move();
			GameHistory(0);
			Move ret = selectMove(ply + 1, depth - 3, -side, -beta, -beta + 1,
					quiesce);
			GameHistory(-1);
			ret.score *= -1;
			if (ret.score >= beta) {
				stat.null_moves++;
				return ret;
			}
			if (ret.score < -WIN_SCORE + 100)
				depth++;
		}

		
		if (ply > 1 && depth > 2 && !quiesce && pv1 == 0 && alpha + 1 < beta) {
			Move ret = selectMove(ply, depth - 2, side, alpha, beta, quiesce);
			pv1 = ret.pvCode();
		}

		
		// Just use the SBE when we reach the bottom of the search;
		// if( depth <= 0 ){
		// best_move.score = sbe.evaluate( this );
		// return best_move;
		// }
		if (depth <= 0 && !quiesce) {
			quiesce = true;
			depth = 0;
		} else if (ply >= MAX_PLY || (max_depth == 1 && ply > max_depth + 3)) {
			best_move.score = evaluate(side, ply);
			return best_move;
		}
		if (quiesce) {
			nscore = best_move.score = evaluate(side, ply);
			if (best_move.score > alpha)
				alpha = best_move.score;
			if (alpha >= beta)
				return best_move;
			if (depth <= 0)
				depth = 0;
		}

		// If we have already lost, then just return a losing score
		// removed June 18,1999 by RL
		// if( sbe.evaluate( this ) == -( WIN_SCORE * side ) ){
		// best_move.score = -( WIN_SCORE * side )+ply;
		// return best_move;
		// }

		// debug (RL)
		// System.out.println("ply: "+ ply + " " + alpha + " " + beta);

		// Get the list of possible moves from the move generator.
		Move move_list = generator.generateMoves(this, side);
		// if (depth == 2)
		// for(Move m2=move_list;m2!=null;m2=m2.next)
		// System.out.println(" "+m2.score+"-");
		move_list = sort(move_list, side, ply, pv1);
		// if (depth == 2)
		// for(Move m3=move_list;m3!=null;m3=m3.next)
		// System.out.print(" "+m3.score+"-");

		// debug
		// if (ply == 1 && max_depth == 1) {
		// String s = "";
		// for( Move m = move_list; m != null; m = m.next )
		// s = s + "  " + m.string();
		// System.out.println("debug:" + s);
		// }

		if (hc && !quiesce && ply > 1 && max_depth > 3 && hashTable.isSet()) {
			for (Move m = move_list; m != null; m = m.next) {
				pv.set(ply + 1);
				m.apply(this);
				int[] p = hashTable.probe(hashKey, ply + 1, depth - 1, -side,
						-beta, -alpha, random_score);
				int t_alpha = p[0];
				int t_beta = p[1];
				boolean found = false;
				switch (p[2]) {
				case HashTable.EXACT:
					if (t_alpha < t_beta) {
						Move nm = new Move(p[3]);
						pv.append(nm, ply + 1);
					}
					m.score = -t_alpha;
					found = true;
					break;
				case HashTable.LOWER:
					m.score = -t_beta;
					found = true;
					break;
				case HashTable.UPPER:
					m.score = -t_alpha;
					found = true;
					break;
				}
				m.undo(this);
				if (found && m.score >= beta) {
					hashTable.store(hashKey, m.pvCode(), ply, depth, side,
							HashTable.UPPER, m.score, random_score);
					history(m, ply, depth);
					pv.add(m, ply);
					stat.hash_cutoff++;
					return m;
				}
			}
		}

		
		boolean first_move = true;

		// Look at every possible move.
		for (Move m = move_list; m != null; m = m.next) {

			
			int ext = 0;

			if (ply == 1 && !pondering) {
				random_score = rnd.nextInt() % 6;
				// System.out.println("random: " + random_score);
			}

			
			current_move[ply] = m;

			
			if (quiesce && m.captured() == 0 && m.promote() == 0)
				continue;

			
			if (quiesce
					&& VAL[m.captured() + 6] + VAL[m.promote() + 6] + nscore
							+ PAWN_VAL < alpha)
				continue;

			
			// if (m.captured() == R_KING*side) {
			if (m.captured() == R_KING || m.captured() == B_KING) {
				m.score = WIN_SCORE - (ply + 1);
				pv.append(m, ply);
				if (hashTable.isSet())
					hashTable.store(hashKey, m.pvCode(), ply, depth, side,
							HashTable.EXACT, m.score, random_score);
				history(m, ply, depth);
				return m;
			}

			
			
			
			if (!quiesce
					&& current_move[ply - 1].captured() != 0
					&& m.captured() != 0
					&& ((current_move[ply - 1].to_row() == m.to_row() && current_move[ply - 1]
							.to_col() == m.to_col()) || VAL[current_move[ply - 1]
							.captured() + 6] <= VAL[m.captured() + 6]))
				ext = 1;

			// Apply the move.
			m.apply(this);

			// See how our opponent can respond.
			
			// Move opponent_move = selectMove(ply+1, depth - 1, -side,
			// best_move.score, prune_thresh );
			Move opponent_move;
			if (first_move) {
				opponent_move = selectMove(ply + 1, depth + ext - 1, -side,
						-beta, -alpha, quiesce);
			} else {
				opponent_move = selectMove(ply + 1, depth + ext - 1, -side,
						-alpha - 1, -alpha, quiesce);
				if (-opponent_move.score > alpha && -opponent_move.score < beta
						&& !timeout_flag && !end_search)
					opponent_move = selectMove(ply + 1, depth + ext - 1, -side,
							-beta, -alpha, quiesce);
			}

			// Undo the move (so that we can apply another one)
			m.undo(this);
			current_move[ply] = null;

			if (ply == 1 && !timeout_flag)
				root_first_move = true;

			// If this gives us a better outcome, take this move.
			
			// if( side * opponent_move.score > side * best_move.score ){
			// best_move = m;
			// best_move.score = opponent_move.score;
			// }
			
			
			if (-opponent_move.score > best_move.score && !timeout_flag) {
				best_move = m;
				best_move.score = -opponent_move.score;
				
				pv.add(best_move, ply);
				if (best_move.score > alpha)
					alpha = best_move.score;

				// if (ply == 1)
				// System.out.println( "PV: " + best_move.score + " " +
				// pv.display());
			}

			
			if (timeout_flag || end_search)
				return best_move;

			// debug
			// if (first_move && beta > alpha+1)
			// System.out.println( "debug: " + ply + " " +
			// best_move.score + " " + alpha + " " + beta);
			// if (ply==1)
			// System.out.println( "debug: " + m.string() + " " +
			// -opponent_move.score);

			
			// if(best_move.score * side > prune_thresh * side)
			// return best_move;
			if (alpha >= beta) {
				
				stat.failed_high++;
				if (first_move)
					stat.failed_high_first++;
				if (!quiesce && hashTable.isSet()) {
					hashTable.store(hashKey, best_move.pvCode(), ply, depth,
							side, HashTable.UPPER, best_move.score,
							random_score);
				}
				history(best_move, ply, depth);
				return best_move;
			}

			
			first_move = false;

		}

		// If there are no moves, just return the SBE
		if (!best_move.defined() && !quiesce) {
			
			// best_move.score = sbe.evaluate( this );
			best_move.score = evaluate(side, ply);
		}

		
		if (best_move.score <= o_alpha) {
			stat.failed_low++;
		}
		if (!quiesce && hashTable.isSet()) {
			hashTable.store(hashKey, best_move.pvCode(), ply, depth, side,
					best_move.score <= o_alpha ? HashTable.LOWER
							: HashTable.EXACT, best_move.score, random_score);
		}

		history(best_move, ply, depth);

		return best_move;
	}

	public Move sort(Move list, int side, int ply, int pv1) {
		Move m;
		// Move sorted = new Move();
		Move sorted = null;
		Move copy = list;

		// gives the SBE value for each move and put it in the score field
		for (m = list; m != null; m = m.next) {
			m.apply(this);

			m.score = -12000000;
			if (pv.check(m, ply) || (pv1 != 0 && m.pvCode() == pv1))
				m.score += 1000000;
			else if (m.captured() != 0 || m.promote() != 0) {
				m.score += (VAL[m.captured() + 6] + VAL[m.promote() + 6] + 500000);
				if (m.captured() == B_KING || m.captured() == R_KING)
					m.score += 5000000;
			} else {
				int pvc = m.pvCode();
				if (pvc == killer_move1[ply])
					m.score += 200000;
				else if (pvc == killer_move2[ply])
					m.score += 120000;
				else if (pvc == killer_move3[ply])
					m.score += 110000;
				else if (pvc == killer_move4[ply])
					m.score += 100000;
				else
					m.score += move_history[m.index()];
			}

			m.undo(this);
		}

		while (list != null) {
			Move front = list;
			list = list.next;
			boolean inserted = false;

			// Figure out where to insert front into sorted.
			if (sorted == null) {
				sorted = front;
				sorted.next = null;
			} else {
				if (sorted.score < front.score) {
					front.next = sorted;
					sorted = front;
				} else {
					Move sv = sorted;
					while (sorted.next != null && !inserted) {
						if (sorted.next.score > front.score)
							sorted = sorted.next;
						else {
							front.next = sorted.next;
							sorted.next = front;
							inserted = true;
						}
					}
					if (!inserted) {
						sorted.next = front;
						front.next = null;
					}
					sorted = sv;
				}

			}

		}
		return sorted;
	}

	
	/**
	 * End current search.
	 */
	public void stop() {
		end_search = true;
	}

	static public void stopAll() {
		stopped = true;
	}

	static public void startAll() {
		stopped = false;
	}

	
	/**
	 * Status of the current search.
	 */
	public boolean thinking() {
		return thinking_flag;
	}

	
	/**
	 * Status of the current search.
	 */
	public void history(Move m, int ply, int depth) {
		int pvc = m.pvCode();
		move_history[m.index()] = depth * depth;
		if (killer_move1[ply] != pvc) {
			killer_move4[ply] = killer_move3[ply];
			killer_move3[ply] = killer_move2[ply];
			killer_move2[ply] = killer_move1[ply];
			killer_move1[ply] = pvc;
		}
	}

	
	/**
	 * Score for current position.
	 */
	public int evaluate(int side, int ply) {
		int score = sbe.evaluate(this) * side;
		if (score < WIN_SCORE && score > -WIN_SCORE)
			score += 10 + random_score * side;
		if (score == WIN_SCORE)
			score = WIN_SCORE - ply;
		else if (score == -WIN_SCORE)
			score = -WIN_SCORE + ply;

		return score;
	}

	
	public void GameHistory(int pvc) {
		if (pvc < 0) {
			history_count--;
			return;
		}
		history_count++;
		history_move[history_count] = pvc;
		history_hash[history_count] = hashKey;
	}

	
	public boolean GameRepeat() {
		int i = history_count - 2;
		while (i >= 0) {
			if (history_hash[i] == hashKey)
				return true;
			i -= 2;
		}
		return false;
	}

	
	public boolean GameRepeat3() {
		int i = history_count, j = 0;

		while (i >= 0) {
			if (history_hash[i] == hashKey)
				if (++j >= 3)
					return true;
			i -= 2;
		}
		return false;
	}

	public boolean Hint(Move m) {
		return hint != 0 && m != null && hint == m.pvCode();
	}

	public void Switch() {
		if (pondering && !puzzling) {
			pondering = false;
			predicted = true;
			search_start_time = System.currentTimeMillis();
		}
	}

	public void ponder(int side) {
		Move move_list = legalMoves(-side);
		Move hint_move = null, m = null;

		puzzling = false;
		pondering = false;
		thinking_flag = false;

		if (DEBUG)
		System.out.println("pondering:");

		if (move_list == null)
			return;

		pondering = true;
		if (hint != 0) {
			for (m = move_list; m != null && m.pvCode() != hint; m = m.next)
				;
			if (m == null)
				hint = 0;
			else {
				m.apply(this);
				if (DEBUG)
				System.out.println("ponder: " + m.string());
			}
		}

		if (hint == 0) {
			puzzling = true;
			if (DEBUG)
			System.out.println("puzzling: ");
			computerMove(-side);
			puzzling = false;
		}

		if (hint != 0) {
			computerMove(side);
		}
		pondering = false;
	}

	public void setHashTable(HashTable tb) {
		hashTable = tb;
	}

	public void setTimeLimit(int tm) {
		timeLimit = tm;
	}

	public void setEPsq(int sq) {
		ep_sq = sq;
	}

	public int getEPsq() {
		return ep_sq;
	}

	public Move LastMove() {
		return lastMove;
	}
}
