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

import java.util.HashMap;
import java.awt.*;
import java.awt.event.*;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

/**
 * A java component used to display a connect-four board.
 */
class BoardDisplay extends JPanel implements Runnable, MouseListener {
	// Size of each square.
	private static final int SQUARE_WIDTH = 50;
	private static final int SQUARE_HEIGHT = 50;
	private static final int MARGIN = 16;

	// Images for displaying the board contents (chess needs a lot of them)
	private HashMap<Integer, Image> images = new HashMap<Integer, Image>();

	// Board object that this is responsible for displaying
	private Board my_board;

	// Added June 16, 1999 by RL
	private Board run_board;
	private Board ponder_board;
	private Move lastMove;

	// Applet containing this board display
	private ChessWindow my_appl;

	// Thread used to let the computer choose and make its move.
	private Thread computer_thread = null;
	private Thread ponder_thread = null;
	private Thread my_thread = null;

	// Which side gets to make the next move.
	// Zero when the game is over.
	private int turn = Board.R_SIDE;

	// Which side is the human?
	private int human_side = Board.R_SIDE;

	// Currently selected row and column
	private int selected_row = -1, selected_col;

	private boolean flip_board = false;
	private MoveSet availableMoves = null;
	private PositionSet availablePieces = null;

	private Font letterFont = new Font(Font.SANS_SERIF, Font.PLAIN, 10);
	private static final String[] letters = new String[] { "a", "b", "c", "d",
			"e", "f", "g", "h" };

	private Image boardImage = null;

	public BoardDisplay(Board board, ChessWindow appl) {

		// Remember the applet that created us.
		my_appl = appl;

		// Remember the image we are supposed to display
		my_board = board;

		// RL
		addMouseListener(this);

		// RL July 10, 1999
		// setImage(1);
		// rr_pawn_image = null;
		images.clear();
		loadImage();

	}

	public void destroy() {
		removeMouseListener(this);
	}

	// RL July 10, 199
	private void loadImage() {
		Board board = my_board;
		ChessWindow appl = my_appl;

		// Media Tracker used to regulate image loading.
		MediaTracker tracker = new MediaTracker(appl);

		// Load images if we don't have them yet.
		if (images.size() == 0) {
			int rs = Board.R_SIDE, bs = Board.B_SIDE;
			int p = Board.PAWN, r = Board.ROOK, b = Board.BISHOP, n = Board.KNIGHT, q = Board.QUEEN, k = Board.KING, x = Board.EMPTY;

			// Load each of the images we need
			for (int piece : Board.PIECES) {
				// Image image = appl.getImage(appl.getDocumentBase(), "img/" +
				// imageName(piece));
				Image image = (new ImageIcon(getClass().getResource(
						imageName(piece)))).getImage();
				images.put(piece, image);
				tracker.addImage(image, 0);
			}

			// Force the tracker to load all of our images.
			do {
				try {
					tracker.waitForAll();
				} catch (InterruptedException e) {
				}
			} while (!tracker.checkAll());
		}

		// Set the size of this component according to what it contains.
		setSize(SQUARE_WIDTH * Board.WIDTH + MARGIN * 2, SQUARE_HEIGHT
				* Board.HEIGHT + MARGIN * 2);
	}

	/**
	 * Restart the game.
	 */
	public void restart() {
		Board.startAll();

		// Kill of the computer move thread if it's trying to make a move.
		terminateComputerThread();

		// Reset the starting board configuration
		my_board.startingConfig();
		turn = Board.R_SIDE;
		human_side = Board.R_SIDE;
		selected_row = -1;
		flip_board = false;
		loadImage();

		// Redraw with the new configuration
		repaint();

		// Tell the user it's time to move.
		my_appl.setStateMessage("Select a move.");

		lastMove = null;

		// startPondering();
	}

	/**
	 * Flip the board.
	 */
	public void flip() {
		// Kill of the computer move thread if it's trying to make a move.
		terminateComputerThread();

		// Switch sides and switch who has the current turn.
		human_side = -human_side;
		turn = -turn;

		flip_board = !flip_board;

		switchTurns();
	}

	@Override
	public int getWidth() {
		return Board.WIDTH * SQUARE_WIDTH + MARGIN * 2;
	}

	@Override
	public int getHeight() {
		return Board.HEIGHT * SQUARE_HEIGHT + MARGIN * 2;
	}

	/**
	 * Paint a representation of the current board state.
	 */
	@Override
	public void paintComponent(Graphics g) {
		if (boardImage == null) {
			boardImage = this.createImage(getWidth(), getHeight());
		}
		drawBoard(boardImage.getGraphics());
		g.drawImage(boardImage, 0, 0, this);
	}

	public void drawBoard(Graphics g) {
		int x, y;

		g.setColor(Color.white);
		g.fillRect(0, 0, getWidth(), getHeight());

		if (flip_board)
			y = SQUARE_HEIGHT * (Board.HEIGHT - 1) + MARGIN;
		else
			y = MARGIN;
		for (int row = 0; row < Board.HEIGHT; row++) {

			g.setColor(Color.black);
			g.setFont(letterFont);
			g.drawString(new Integer(row + 1).toString(), 6, getHeight()
					- (row + 1) * SQUARE_HEIGHT + 8);
			g.drawString(new Integer(row + 1).toString(), getWidth() - 12,
					getHeight() - (row + 1) * SQUARE_HEIGHT + 8);
			g.drawString(letters[row], (row + 1) * SQUARE_WIDTH - 12, 12);
			g.drawString(letters[row], (row + 1) * SQUARE_WIDTH - 12,
					getHeight() - 6);

			if (flip_board)
				x = SQUARE_WIDTH * (Board.WIDTH - 1) + MARGIN;
			else
				x = MARGIN;
			for (int col = 0; col < Board.WIDTH; col++) {
				// Fill in this space with the appropariate image
				Image img = null;

				// changed June 15, 1999 by RL
				// int sym = my_board.space[row][col];
				int sym = my_board.getSpace(row, col);

				// Decide which image we should use for this space.
				if (sym != Board.EMPTY)
					img = images.get(sym);

				Color color;
				// Draw the chosen image.
				if ((row + col) % 2 == 0)
					color = new Color(0.8f, 0.8f, 0.8f);
				else
					color = new Color(0.5f, 0.5f, 0.5f);

				if (availableMoves != null) {
					if (availableMoves.containsFrom(row, col)) {
						color = mixColors(color, Color.cyan);
					} else if (availableMoves.containsTo(row, col)) {
						color = mixColors(color, Color.yellow);
					}
				}

				if (availablePieces != null) {
					if (availablePieces.contains(row, col)) {
						color = mixColors(color, Color.cyan);
					}
				}

				g.setColor(color);
				g.fillRect(x, y, SQUARE_WIDTH, SQUARE_HEIGHT);

				if (img != null)
					g.drawImage(img, x + 10, y - 10, my_appl);

				/*
				 * if (selected_row == row && selected_col == col) {
				 * g.setColor(Color.green); g.drawRect(x, y, IMAGE_WIDTH - 1,
				 * IMAGE_HEIGHT - 1); g.drawRect(x + 1, y + 1, IMAGE_WIDTH - 3,
				 * IMAGE_HEIGHT - 3); }
				 * 
				 * if (lastMove != null) { if (lastMove.from_row() == row &&
				 * lastMove.from_col() == col) { g.setColor(Color.blue);
				 * g.drawRect(x, y, IMAGE_WIDTH - 1, IMAGE_HEIGHT - 1);
				 * g.drawRect(x + 1, y + 1, IMAGE_WIDTH - 3, IMAGE_HEIGHT - 3);
				 * } else if (lastMove.to_row() == row && lastMove.to_col() ==
				 * col) { g.setColor(Color.red); g.drawRect(x, y, IMAGE_WIDTH -
				 * 1, IMAGE_HEIGHT - 1); g.drawRect(x + 1, y + 1, IMAGE_WIDTH -
				 * 3, IMAGE_HEIGHT - 3); } }
				 */

				// Step ahead to the next column on the screen.
				if (flip_board)
					x -= SQUARE_WIDTH;
				else
					x += SQUARE_WIDTH;
			}

			// Step ahead to the next row on the screen
			if (flip_board)
				y -= SQUARE_HEIGHT;
			else
				y += SQUARE_HEIGHT;
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	//
	// Respond to user mouse clicks, generally, when the user is making
	// a move.
	//
	// public boolean mouseDown(Event evt, int x, int y) {

	@Override
	public void mouseReleased(MouseEvent e) {
		int x, y;
		if (flip_board) {
			y = SQUARE_HEIGHT * Board.HEIGHT - e.getY();
			x = SQUARE_WIDTH * Board.WIDTH - e.getX();
		} else {
			y = e.getY();
			x = e.getX();
		}

		// Don't let the user choose anything if the game is
		// over or if it's the computer's turn to move.
		if (turn != human_side)
			return;

		// First, figure out row and column of the selected space
		int row = y / SQUARE_HEIGHT;
		int col = x / SQUARE_WIDTH;

		// Make sure the selected space is actually on the board
		if (row < 0 || col < 0 || row >= Board.HEIGHT || col >= Board.WIDTH)
			return;

		// See if this is the choice for the source or destination position.
		if (selected_row >= 0) {
			// See if the user is trying to un-select the chosen space.
			if (row == selected_row && col == selected_col) {
				// Flag the current selection as empty.
				selected_row = -1;
				repaint();
			} else {
				// Get the list of all legal moves from the current board.
				Move move_list = my_board.legalMoves(turn);
				Move m;

				// Look for the user's choice on that list.
				// changed June 16, 1999 by RL
				// change reference to from_row, from_col, to_row, to_col as
				// methods
				// for( m = move_list; m != null &&
				// ( m.from_row() != selected_row || m.from_col() !=
				// selected_col ||
				// m.to_row() != row || m.to_col() != col );
				// m = m.next );
				for (m = move_list; m != null
						&& (m.from_row() != selected_row
								|| m.from_col() != selected_col
								|| m.to_row() != row || (m.to_col() != col && (m
								.piece() != Board.KING * human_side || (((selected_col - 2 != col && col != 0) || m
								.from_col() - 2 < m.to_col()) && ((selected_col + 2 != col && col != Board.WIDTH - 1) || m
								.from_col() + 2 > m.to_col()))))); m = m.next)
					;

				// If the chosen move is legal, apply it.
				if (m != null) {
					m.apply(my_board);
					lastMove = m;

					// Turn off the currently selected position.
					selected_row = -1;

					// Try to let the computer have a turn.
					switchTurns();
				}
				// RLL
				else if (my_board.getSpace(row, col) * human_side == -Board.KING
						&& my_board.getSpace(selected_row, selected_col)
								* human_side == Board.KING) {
					// toggleImage();
				}
			}
		} else {
			// Make sure the user selected a space occupied by one of their
			// pieces.
			// changed June 15, 1999 by RL
			// if( my_board.space[ row ][ col ] * human_side >= 1 ){
			if (my_board.getSpace(row, col) * human_side >= 1) {
				selected_row = row;
				selected_col = col;
				repaint();
			}
		}

	}

	public int getTurn() {
		return turn;
	}

	public void switchTurns() {
		int winner = my_board.checkForWin();

		// See if either side has won.
		if (winner != 0) {
			turn = 0;

			// changed June 23,1999 by RL
			// win/lose/draw
			if (winner == -human_side)
				my_appl.setStateMessage("Game over. You lose!");
			else if (winner == human_side)
				my_appl.setStateMessage("Game over. You Win!");
			else
				my_appl.setStateMessage("Game over. It's a Draw!");
		} else {
			// No winner yet. Can we switch Turns?
			if (my_board.legalMoves(-turn) != null)
				// We can switch turns.
				turn = -turn;
			else
			// See if there are any legal moves left.
			if (my_board.legalMoves(turn) == null)
				turn = 0;

			// Show a message for the current state.
			if (turn == 0)
				my_appl.setStateMessage("Game over. It's a tie.");

			if (turn == -human_side) {
				my_appl.setStateMessage("Selecting computer move.");
				if (ponder_thread != null && ponder_board.Hint(lastMove))
					startThinking();
				else
					startComputerMove();
			}

			if (turn == human_side) {
				my_appl.setStateMessage("Select your move.");
				startPondering();
			}

		}

		repaint();
	}

	//
	// Terminate the computer thread early, before it has completed
	// making its move.
	//
	private void terminateComputerThread() {
		if (computer_thread != null) {
			// added June 16,1999 by RL
			if (run_board != null)
				run_board.stop();
			// computer_thread.stop();
			computer_thread = null;
		}
		terminatePonderThread();
	}

	//
	// Put the move made by the computer into the current board.
	// This is a separate method so that it can be synchronized.
	//
	private void submitComputerMove(Board alt_board) {

		// Switch over to the new alt_board.
		my_board = alt_board.Duplicate(my_board);

		lastMove = my_board.LastMove();

		// See if the game is over
		switchTurns();

		// Flag that the computer move is done.
		computer_thread = null;

		if (my_board.gameListener != null)
			my_board.gameListener.move(lastMove);

		// RL
		System.gc();
	}

	//
	// Select a new computer move, then exit.
	// Computer moves are made in a separate thread. That way, we don't
	// tie up the user's thread for other UI activity.
	//
	// change June 16, 1999 by RL
	// change alt_board to run_board
	@Override
	public void run() {

		Thread tmp = my_thread;

		if (ponder_thread == my_thread) {
			ponder_board = my_board.Duplicate(null);

			ponder_board.ponder(-human_side);
		} else if (computer_thread == my_thread) {
			// Make a scratch copy of the current board to work with.
			// Board alt_board = my_board.Duplicate();
			run_board = my_board.Duplicate(null);

			// Now, let the computer choose a response.
			run_board.computerMove(-human_side);
		}

		if (computer_thread == tmp) {
			// Apply the computer's move to the main board.
			submitComputerMove(run_board);
		} else if (ponder_thread == tmp)
			ponder_thread = null;

	}

	/**
	 * Start up a spare thread and use it to let the computer choose it's move.
	 */
	private void startComputerMove() {
		// Update the state of the applet.
		my_appl.disableDefer();

		terminatePonderThread();

		// Make a new thread in which the computer can choose
		// its move.
		computer_thread = new Thread(this);
		my_thread = computer_thread;
		computer_thread.start();
	}

	/**
	 * Start up a spare thread and use it to let the computer pondering on
	 * opponent's time.
	 */
	private void startPondering() {
		ponder_thread = new Thread(this);
		my_thread = ponder_thread;
		ponder_thread.start();
	}

	private void terminatePonderThread() {
		if (ponder_thread != null) {
			if (ponder_board != null)
				ponder_board.stop();
			// ponder_thread.stop();
			ponder_thread = null;
		}
	}

	private void startThinking() {
		Board tmp = run_board;
		run_board = ponder_board;
		ponder_board = tmp;
		computer_thread = ponder_thread;
		ponder_thread = null;
		run_board.Switch();
	}

	public void stop() {
		Board.stopAll();
		terminateComputerThread();
	}

	private String imageName(int piece) {
		switch (piece) {
		case Board.B_PAWN:
			return "black_pawn.gif";
		case Board.R_PAWN:
			return "white_pawn.gif";
		case Board.B_ROOK:
			return "black_rook.gif";
		case Board.R_ROOK:
			return "white_rook.gif";
		case Board.B_BISHOP:
			return "black_bishop.gif";
		case Board.R_BISHOP:
			return "white_bishop.gif";
		case Board.B_KNIGHT:
			return "black_knight.gif";
		case Board.R_KNIGHT:
			return "white_knight.gif";
		case Board.B_QUEEN:
			return "black_queen.gif";
		case Board.R_QUEEN:
			return "white_queen.gif";
		case Board.B_KING:
			return "black_king.gif";
		case Board.R_KING:
			return "white_king.gif";
		}
		return null;
	}

	public void setAvailableMoves(MoveSet currentMoves) {
		this.availablePieces = null;
		this.availableMoves = currentMoves;
	}

	public void setAvailablePieces(PositionSet currentPieces) {
		this.availablePieces = currentPieces;
		this.availableMoves = null;
	}

	private static Color mixColors(Color color1, Color color2) {
		return new Color((color1.getRed() + color2.getRed()) / 2,
				(color1.getGreen() + color2.getGreen()) / 2,
				(color1.getBlue() + color2.getBlue()) / 2);
	}
}
