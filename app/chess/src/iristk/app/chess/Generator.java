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
 * Abstract class containing the a move generator for a particular game.
 */
public abstract class Generator {
	/**
	 * Generate a list of legal moves from the given board configuration.
	 */
	public abstract Move generateMoves(Board b, int side);
}
