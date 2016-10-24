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
package iristk.cfg;

import java.util.List;

public abstract class Edge {

	protected Integer begin;
	protected Integer end;
	
	public abstract List<Edge> matches(Edge passive);

	public abstract boolean isPassive();
	
	public abstract boolean isActive();

	public Integer getBegin() {
		return begin;
	}

	public Integer getEnd() {
		return end;
	}

	public abstract List<Word> getWords();
	
	public abstract int getLevel();

	
}

