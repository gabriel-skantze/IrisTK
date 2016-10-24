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
package iristk.system;

import iristk.util.NameFilter;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Timestamp;

public class EventLogger implements EventListener {

	private OutputStream out;
	private NameFilter filter;
	
	public EventLogger(OutputStream out, NameFilter filter) {
		this.out = out;
		this.filter = filter;
	}
		
	public EventLogger(OutputStream out, String filter) {
		this(out, NameFilter.compile(filter));
	}
	
	public synchronized void newOutputStream(OutputStream out) {
		this.out = out;
	}
	
	public synchronized void closeOutputStream() {
		try {
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		out = null;
	}
		
	@Override
	public synchronized void onEvent(Event event) {
		if (out != null && filter.accepts(event.getName())) {
			try {
				//out.write(new String(event.toXmlString() + "\n").getBytes());
				event.put("log_time", new Timestamp(System.currentTimeMillis()).toString());
				out.write(new String(event.toJSON() + "\n").getBytes());
				out.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}	
		}
	}

}
