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
package iristk.app.quiz;

import iristk.system.IrisUtils;
import iristk.util.RandomList;

import java.io.*;
import java.util.*;

import org.slf4j.Logger;

public class QuestionSet extends ArrayList<Question> {
	
	private static Logger logger = IrisUtils.getLogger(QuestionSet.class);
	
	private int n = 0;
	
	public QuestionSet(InputStream questionFile) throws IOException {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(questionFile));
			String line = br.readLine();
			int qn = 0;
			int ln = 0;
			while ((line = br.readLine()) != null) {
				ln++;
				if (!line.matches("[A-Za-z0-9,\\.;\\?'\\- ]*")) {
					System.err.println("Illegal line " + ln + ": " + line);
					continue;
				}
				String[] cols = line.split(";");
				if (cols.length < 5) {
					logger.warn("Not enough columns in line " + ln + ": " + line);
					continue;
				} 
				Question q = new Question("q" + qn++, cols);
				add(q);
			}
			logger.info(qn + " questions read");
			randimize();
		} catch (IOException e) {
			throw new IOException("Problem reading questions: " + e.getMessage());
		}
	}
	
	public QuestionSet(File file) throws IOException  {
		this(new FileInputStream(file));
	}

	public void randimize() {
		RandomList.shuffle(this);
	}
	
	public Question next() {
		Question q = get(n);
		n++;
		if (n >= size()) {
			n = 0;
		}
		return q;
	}
	
}
