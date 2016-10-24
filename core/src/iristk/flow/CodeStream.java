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

import java.io.OutputStream;
import java.io.PrintStream;

public class CodeStream extends PrintStream {

	private int indent = 0;
	private String lastLine;
	
	public CodeStream(OutputStream out) {
		super(out);
	}
	
	@Override
	public void println(String string) {
		string = string.trim();
		if (string.contains("\n")) {
			for (String line : string.split("\n")) {
				println(line);
			}
		} else {
			if (string.startsWith("break") && lastLine.startsWith("break"))
				return;
			lastLine = string;
			int ind = indent;
			if (string.startsWith("}")) {
				ind--;
			}
			for (int i = 0; i < ind; i++) {
				super.print("\t");
			}
			super.println(string);
			indent += count(string, "{") - count(string, "}");
		}
	}

	public static int count(String string, String substr) {
		int n = 0;
		int p = string.indexOf(substr, 0);
		while (p != -1) {
			n++;
			p = string.indexOf(substr, p + 1);
		}
		return n;
	}
	
}
