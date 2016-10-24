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

import java.util.List;

import iristk.util.Record;

public class RecHyp extends Record {

	public RecHyp() {
	}
	
	public RecHyp(String text) {
		this.text = text;
	}
	
	@RecordField(name="text")
	public String text = "";
	@RecordField(name="conf")
	public Float conf = 1.0f;
	@RecordField(name="sem")
	public Record sem = null;
	@RecordField(name="words")
	public List<Word> words = null;
	
}
