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

import iristk.cfg.SRGSGrammar;
import iristk.flow.FlowModule;
import iristk.speech.SpeechGrammarContext;
import iristk.speech.windows.WindowsRecognizerFactory;
import iristk.speech.windows.WindowsSynthesizer;
import iristk.system.Event;
import iristk.system.SimpleDialogSystem;
import iristk.util.Language;

public class ChessSystem implements GameListener {

	private FlowModule flowModule;
	private ChessGame chessGame;

	public ChessSystem() throws Exception {
		// Create the system
		SimpleDialogSystem system = new SimpleDialogSystem(this.getClass());
		// Set the language of the system
		system.setLanguage(Language.ENGLISH_US);
		// Set up the GUI
		system.setupGUI();
		
		// Add the recognizer to the system
		system.setupRecognizer(new WindowsRecognizerFactory());
		//system.setupConsoleRecognizer();
		system.getRecognizerModule().setPartialResults(true);
		
		// Add a synthesizer to the system		
		system.setupSynthesizer(new WindowsSynthesizer());
		//system.connectToBroker("furhat", "169.254.117.193");
		//system.setupFace();
				
		// Set up the chess game
		chessGame = new ChessGame(system.getGUI());
		// Listen for events in the chess game
		chessGame.setGameListener(this);
		chessGame.start();
		// Add the flow and give it access to the chess game
		flowModule = new FlowModule(new ChessFlow(chessGame));
		system.addModule(flowModule);
		// Load a grammar in the recognizer
		system.loadContext("default", new SpeechGrammarContext(new SRGSGrammar(getClass().getResource("ChessGrammar.xml").toURI())));
		//system.loadContext("default", new SemanticGrammarContext(new SRGSGrammar(getClass().getResource("ChessGrammar.xml").toURI())));
		
		// Start the interaction
		system.sendStartSignal();
	}
	
	@Override
	public void tentativeMove(Move move) {
	}

	@Override
	public void move(Move move) {
		// The system made a move, inform the flow
		flowModule.invokeEvent(new Event("chess.move.system"));
	}

	@Override
	public void gameRestart() {
		flowModule.invokeEvent(new Event("chess.restart"));
	}
	
	public static void main(String[] args) throws Exception {
		new ChessSystem();
	}

}
