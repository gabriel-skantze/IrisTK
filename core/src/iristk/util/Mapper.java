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

import iristk.system.IrisUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import org.slf4j.Logger;

public class Mapper extends HashMap<String,String> {
	
	private static Logger logger = IrisUtils.getLogger(Mapper.class);

	private String name;

	public Mapper(String name, InputStream inputStream) {
		this.name = name;
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			String line;
			while ((line = reader.readLine()) != null) {
				String[] cols = line.trim().split("\\s+");
				add(cols);
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void add(String... items) {
		if (items.length >= 2) {
			for (int i = 1; i < items.length; i++) {
				put(items[i], items[0]);
			}
		} else {
			put(items[0], null);
		}
	}

	public String map(String label) {
		if (containsKey(label)) {
			return get(label);
		} else {
			logger.warn(name + " could not map '" + label + "'");
			return null;
		}
	}

	public String map(String label, String def) {
		label = map(label);
		if (label == null) {
			logger.warn(name + " could not map '" + label + "'");
			return def;
		} else {
			return label;
		}
	}

}
