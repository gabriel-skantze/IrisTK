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

import java.awt.*;
import java.awt.event.*;

import javax.swing.JPanel;

public class ChessWindow extends JPanel implements ActionListener, ItemListener {
	
	// Current board configuration
	public Board board;

	// Object responsible for displaying this board
	public BoardDisplay boardDisplay;

	// Buttons to start the game over and to defer the first move.
	// And to invert the board.
	private Button restart_btn, show_monitor_btn, defer_btn;

	// Message to display to the user, depending on the state of the
	// system.
	private TextField state_field;

	// RL July 5, 1999
	private Choice hash_size, time_limit;
	private HashTable hashTable;

	//public GameListener gameListener = null;
	
	//
	// Create all of the UI elements in this game.
	//
	public void init() {
		//setTitle("Chess Board");

		// Use gridbag layout so we can enforce different layout constraints
		// on each object in the applet.
		GridBagLayout gridbag = new GridBagLayout();
		setLayout(gridbag);

		// Make default layout constraints
		GridBagConstraints gc = new GridBagConstraints();
		gc.insets = new Insets(3, 3, 3, 3);
		gc.weightx = 1;

		// Make a new board and a component to display it
		board = new Board(new ChessGenerator(), new ChessEvaluator(), null);
		boardDisplay = new BoardDisplay(board, this);

		setSize(boardDisplay.getWidth() + 20, boardDisplay.getHeight() + 100);
		//setResizable(false);

		// Board itself does not stretch to fill its region
		gc.fill = GridBagConstraints.NONE;
		gc.gridwidth = GridBagConstraints.REMAINDER;
		gridbag.setConstraints(boardDisplay, gc);
		add(boardDisplay);

		// Make a text field to display state information
		// setFont( new Font("times", Font.PLAIN, 14) );
		setFont(new Font("times", Font.PLAIN, 10));
		state_field = new TextField();
		state_field.setEditable(false);

		// State field and restart button will stretch horizontally
		gc.fill = GridBagConstraints.HORIZONTAL;
		gc.gridwidth = GridBagConstraints.RELATIVE; // RLL
		gridbag.setConstraints(state_field, gc);
		add(state_field);

		// RL July 9, 1999
		time_limit = new Choice();
		time_limit.add("180 Sec/Move");
		time_limit.add("90 Sec/Move");
		time_limit.add("60 Sec/Move");
		time_limit.add("45 Sec/Move");
		time_limit.add("30 Sec/Move");
		time_limit.add("20 Sec/Move");
		time_limit.add("15 Sec/Move");
		time_limit.add("10 Sec/Move");
		time_limit.add("5 Sec/Move");
		time_limit.select(8);
		time_limit.addItemListener(this);
		gc.gridwidth = GridBagConstraints.REMAINDER;
		gridbag.setConstraints(time_limit, gc);
		add(time_limit);

		board.setTimeLimit(5000); // 5 Sec/move

		// Make a button to restart the game.
		restart_btn = new Button("Restart");
		restart_btn.addActionListener(this);
		gc.gridwidth = 1;
		gridbag.setConstraints(restart_btn, gc);
		add(restart_btn);

		// Make the button to defer the first move.
		show_monitor_btn = new Button("Show Monitor");
		show_monitor_btn.addActionListener(this);
		gridbag.setConstraints(show_monitor_btn, gc);
		show_monitor_btn.setEnabled(false);
		add(show_monitor_btn);

		// Make the button to defer the first move.
		// gc.gridwidth = GridBagConstraints.REMAINDER;
		defer_btn = new Button("Defer Move");
		defer_btn.addActionListener(this);
		gridbag.setConstraints(defer_btn, gc);
		// add(defer_btn);

		// RL July 5, 1999
		hash_size = new Choice();
		hash_size.add("96M");
		hash_size.add("48M");
		hash_size.add("24M");
		hash_size.add("12M");
		hash_size.add("6M");
		hash_size.add("3M");
		hash_size.add("1.5M");
		hash_size.add("750K");
		hash_size.add("Hash Table");
		hash_size.select(8);
		hash_size.addItemListener(this);
		gc.gridwidth = GridBagConstraints.REMAINDER;
		gridbag.setConstraints(hash_size, gc);
		add(hash_size);

		hashTable = new HashTable(0); // default
		board.setHashTable(hashTable);

		// Start with a fresh board.
		// my_board_display.restart();
	}

	/**
	 * Set the currently displayed state-dependent message.
	 */
	public void setStateMessage(String str) {
		state_field.setText(str);
	}

	/**
	 * Disable the defer button so that the human can't skip moves during the
	 * game.
	 */
	public void disableDefer() {
		defer_btn.setEnabled(false);
		hash_size.setEnabled(false);
		time_limit.setEnabled(false);
	}

	//
	// called when something interesting happens to the interface.
	// Used to respond to a click on the restart button.
	//
	// public boolean action( Event evt, Object obj )
	@Override
	public void actionPerformed(ActionEvent evt) {
		if (evt.getSource() == restart_btn) {
			// Restart the game if the user clicks on Restart
			boardDisplay.restart();
			defer_btn.setEnabled(true);
			hash_size.setEnabled(true);
			time_limit.setEnabled(true);
			if (board.gameListener != null)
				board.gameListener.gameRestart();
			return;
		}

		if (evt.getSource() == show_monitor_btn) {
			// jindigoSystem.showSystemWindow();
			return;
		}

		if (evt.getSource() == defer_btn) {
			// Let the computer move first if the user wants to.
			boardDisplay.switchTurns();
			return;
		}

	}
	
	@Override
	public void itemStateChanged(ItemEvent evt) {
		if (evt.getSource() == hash_size) {
			// set hashTable size
			hashTable = null;
			board.setHashTable(null);
			System.gc();
			switch (hash_size.getSelectedIndex()) {
			case 0:
				hashTable = new HashTable(010000000);
				break;
			case 1:
				hashTable = new HashTable(04000000);
				break;
			case 2:
				hashTable = new HashTable(02000000);
				break;
			case 3:
				hashTable = new HashTable(01000000);
				break;
			case 4:
				hashTable = new HashTable(0400000);
				break;
			case 5:
				hashTable = new HashTable(0200000);
				break;
			case 6:
				hashTable = new HashTable(0100000);
				break;
			case 7:
				hashTable = new HashTable(040000);
				break;
			case 8:
				hashTable = new HashTable(0);
				break;
			}
			board.setHashTable(hashTable);
			return;
		}

		if (evt.getSource() == time_limit) {
			int tm = 5000;
			switch (time_limit.getSelectedIndex()) {
			case 0:
				tm = 180000;
				break;
			case 1:
				tm = 90000;
				break;
			case 2:
				tm = 60000;
				break;
			case 3:
				tm = 45000;
				break;
			case 4:
				tm = 30000;
				break;
			case 5:
				tm = 20000;
				break;
			case 6:
				tm = 15000;
				break;
			case 7:
				tm = 10000;
				break;
			case 8:
				tm = 5000;
				break;
			}
			board.setTimeLimit(tm);
			return;
		}
	}

	public void start() {
		boardDisplay.restart();
		setVisible(true);
	}

	public void stop() {
		boardDisplay.stop();
	}

}
