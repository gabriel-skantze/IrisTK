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
package iristk.flow;

public class Timer {

	private long startTime;
	
	public Timer() {
		this.startTime = System.currentTimeMillis();
	}

	public boolean passed(int msec) {
		return (System.currentTimeMillis() - startTime >= msec);
	}

	public void reset() {
		startTime = System.currentTimeMillis();
	}
	
}
