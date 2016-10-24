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
package iristk.speech.nuance9;

import iristk.speech.RecHyp;
import iristk.speech.RecResult;
import iristk.speech.Word;
import iristk.speech.nuance9.xml.Interpretation;
import iristk.speech.nuance9.xml.Result;
import iristk.util.Record;
import iristk.xml.XmlMarshaller;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import com.sun.jna.Pointer;
import com.sun.jna.WString;
import com.sun.jna.ptr.PointerByReference;

public class NuanceResult extends RecResult {

	private Pointer handle;
	private String xmlResult;
	
	static XmlMarshaller<Result> marshaller;
	
	static {
		marshaller = new XmlMarshaller<Result>("iristk.speech.nuance9.xml");
	}
	
	NuanceResult(String type) {
		this.type = type;
	}
	
	NuanceResult(String type, String text) {
		this.type = type;
		this.text = text;
	}
	
	NuanceResult(Pointer handle, boolean makeWords, boolean makeNbest) {
		this.type = RecResult.FINAL;
		this.handle = handle;
		PointerByReference xr = new PointerByReference();
		try {
			BaseRecognizer.call("SWIrecGetXMLResult", SWIrec.INSTANCE.SWIrecGetXMLResult(handle, new WString("application/x-vnd.speechworks.recresult+xml"), xr));
		} catch (NuanceException e) {
			e.printStackTrace();
		}
		xmlResult = xr.getValue().getString(0, true);
		try {
			Result xResult = marshaller.unmarshal(xmlResult.getBytes("utf-8"));
			List<RecHyp> nbest = new ArrayList<>();
			for (Interpretation interp : xResult.getInterpretation()) {
				RecHyp hyp = new RecHyp();
				hyp.conf = interp.getConf();
				hyp.text = interp.getText().getContent();
				if (makeWords) {
					hyp.words = new ArrayList<Word>();
					for (iristk.speech.nuance9.xml.Word word : interp.getInstance().getSWILiteralTimings().getAlignment().getWord()) {
						hyp.words.add(new Word(word.getContent(), word.getConfidence(), (float)word.getStart() / 1000f, (float)word.getEnd() / 1000f));
					}
				}
				if (interp.getInstance() != null && interp.getInstance().getSWIMeaning() != null) {
					String meaning = interp.getInstance().getSWIMeaning().trim();
					if (meaning.length() > 0) {
						Object sem = parseMeaning(meaning);
						if (sem instanceof Record)
							hyp.sem = (Record) sem;
					}
				}
				nbest.add(hyp);
			}
			if (nbest.size() > 0) {
				this.conf = nbest.get(0).conf;
				this.text = nbest.get(0).text;
				this.words = nbest.get(0).words;
				this.sem = nbest.get(0).sem;
				if (makeNbest) {
					this.nbest = nbest;
				}
			}
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		/*
		Matcher m = Pattern.compile("[0-9\\.]+").matcher(xmlResult);
		if (m.find()) {
			setConf(Float.parseFloat(m.group()));
		}
		m = Pattern.compile("<text mode=\"voice\">(.*?)<").matcher(xmlResult);
		if (m.find()) {
			setText(m.group(1));
		}
		*/ catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}


	// {act_move:true movement:{steps:2} piece:{relPiecePos:{piece:{type:Queen} rel:FrontOf} type:Pawn}}
	private static Object parseMeaning(String meaning) {
		if (meaning.startsWith("{"))
			return parseRecord(meaning);
		else {
			return meaning;
		}
	}
	
	public static void main(String[] args) {
		System.out.println(parseMeaning("{act_move:true movement:{steps:2} piece:{relPiecePos:{piece:{type:Queen} rel:FrontOf} type:Pawn}}"));
	}
	
	private static Record parseRecord(String meaning) {
		Record result = new Record();
		meaning = meaning.substring(1, meaning.length() - 1);
		int col = meaning.indexOf(':');
		int pos = 0;
		while (col > -1) {
			String field = meaning.substring(pos, col);
			String value;
			if (meaning.charAt(col+1) == '{') {
				int para = 1;
				pos = col+1;
				while (para > 0) {
					pos++;
					char c = meaning.charAt(pos);
					if (c == '}')
						para--;
					else if (c == '{')
						para++;
				}
				value = meaning.substring(col+1, pos+1);
				pos += 2;
			} else {
				pos = meaning.indexOf(' ', col);
				if (pos == -1)
					pos = meaning.length();
				value = meaning.substring(col+1, pos);
				pos++;
			}
			result.put(field, parseMeaning(value));
			col = meaning.indexOf(':', pos);
		}
		return result;
	}
	
	
	public String toXML() { 
		return xmlResult;
	}
	
	public String toLattice() {
		PointerByReference xr = new PointerByReference(); 
		try {
			BaseRecognizer.call("SWIrecGetXMLResult", SWIrec.INSTANCE.SWIrecGetXMLResult(handle, new WString("application/x-vnd.speechworks.wordlattice+xml"), xr));
		} catch (NuanceException e) {
			e.printStackTrace();
		}
		return xr.getValue().getString(0, true);
	}
	
	//@Override
	//public String toString() {
	//	return super.getText();
	//}
	
}
