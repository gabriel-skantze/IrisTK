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
package iristk.speech;

import iristk.util.Record;

public class Word extends Record {
		
	@RecordField(name="text")
	public String text = "";
	@RecordField(name="conf")
	public Float conf = 1.0f;
	@RecordField(name="start")
	public Float start = null;
	@RecordField(name="end")
	public Float end = null;
	
	public Word() {
	}
	
	public Word(String text, Float conf, Float start, Float end) {
		this.text = text;
		this.conf = conf;
		this.start = start;
		this.end = end;
	}
		
}
