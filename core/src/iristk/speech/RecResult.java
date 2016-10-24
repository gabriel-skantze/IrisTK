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
import java.util.List;

public class RecResult extends RecHyp {
	
	public static final String FINAL = "final";
	public static final String PARTIAL = "partial";
	public static final String SILENCE = "silence";
	public static final String MAXSPEECH = "maxspeech";
	public static final String FAILED = "failed";
	public static final String NOMATCH = "<NOMATCH>";
	
	@RecordField(name="type")
	public String type = FINAL;
	
	@RecordField(name="length")
	public Float length = 0f;
	
	@RecordField(name="nbest")
	public List<RecHyp> nbest = null;
	
	@RecordField(name="grammar")
	public String grammar;
	
	public RecResult() {
	}
	
	public RecResult(String type, String text, float conf, float length, Record sem) {
		this.type = type;
		this.text = text;
		this.conf = conf;
		this.length = length;
		this.sem = sem;
	}

	public RecResult(String type, String text) {
		this.type = type;
		this.text = text;
	}
	
	public RecResult(String type) {
		this.type = type;
	}

	public boolean isNomatch() {
		return text.equals(NOMATCH);
	}
	
	public boolean isFinal() {
		return type.equals(FINAL);
	}
	
	public boolean isPartial() {
		return type.equals(PARTIAL);
	}
	
	public boolean isTimeout() {
		return type.equals(SILENCE);
	}
	
	public boolean isMaxSpeech() {
		return type.equals(MAXSPEECH);
	}
	
	public boolean isFailed() {
		return type.equals(FAILED);
	}

	/*
	public static void main(String[] args) throws Exception {
		RecResult result = new RecResult(FINAL, "hello", 0.5f, 3f, new Record());
		result.words  = new ArrayList<Word>();
		result.words.add(new Word("hello", 0.5f, 1.0f, 2.3f));
		Event event = new Event("rec.final");
		event.putAll(result);
		String xml = EventMarshaller.marshalToString(EventMarshaller.unmarshal(EventMarshaller.marshalToString(event).getBytes()));
		System.out.println(XmlUtils.indentXml(xml));
	}
	*/
	
}
