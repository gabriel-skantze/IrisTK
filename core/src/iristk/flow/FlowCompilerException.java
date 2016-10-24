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

public class FlowCompilerException extends Exception {

	private final int lineNumber;
	
	public FlowCompilerException(String message) {
		this(message, null);
	}
	
	public FlowCompilerException(String message, Integer lineNumber) {
		super(message);
		if (lineNumber == null || lineNumber < 1)
			this.lineNumber = 1;
		else
			this.lineNumber = lineNumber;
	}

	public int getLineNumber() {
		return lineNumber;
	}
	
	
}
