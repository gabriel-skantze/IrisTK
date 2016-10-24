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

public class Word {

	private String word;
	private Double conf;
	
	public Word(String word) {
		this.word = word;
		setConf(1.0);
	}
	
	public Word(String word, double conf) {
		this.word = word;
		setConf(conf);
	}

	@Override
	public String toString() {
		return word;
	}

	public Double getConf() {
		return conf;
	}

	public void setConf(Double conf) {
		this.conf = conf;
	}

	public String getWordString() {
		return word;
	}

}
