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
package iristk.util;

import java.io.ByteArrayOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ParsedInputStream extends FilterInputStream {

	public ParsedInputStream(InputStream stream) {
		super(stream);
	}
	
	public String readTo(int... stopChars) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int b;
		READ:
		while (true) {
			b = super.read();
			if (b == -1) break READ;
			for (int c : stopChars) {
				if (b == c) break READ;
			}
			baos.write(b);
		}
		return baos.toString();
	}
	
	public String readLine() throws IOException {
		return readTo(10, 13);
	}

}
