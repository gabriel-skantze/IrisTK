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
import iristk.util.Record;

public class Event extends Record {
	
	private String name;
	private String sender;
	private String id;
	private String time;
	
	public static final Event REF_EVENT = new Event();
	
	public Event() {
	}
			
	public Event(String name) {
		setName(name);
	}
	
	public Event(String name, Record parameters) {
		setName(name);
		if (parameters instanceof Event)
			copyParams((Event)parameters);
		else
			putAllExceptNull(parameters);
	}
	
	public Event(String name, Object... params) {
		if (params.length % 2 != 0)
			throw new IllegalArgumentException("Must pass an even number of parameters");
		for (int i = 0; i < params.length; i += 2) {
			put(params[i].toString(), params[i+1]);
		}
		setName(name);
	}
	
	public Event(Event event) {
		this(event.getName(), event.getEventParams());
	}
	
	@RecordField(name="event_id")
	public void setId(String id) {
		this.id = id;
	}
	
	@RecordField(name="event_id")
	public String getId() {
		return id;
	}
	
	@RecordField(name="event_sender")
	public void setSender(String sender) {
		this.sender = sender;
	}
	
	@RecordField(name="event_sender")
	public String getSender() {
		return sender;
	}
	
	@RecordField(name="event_name")
	public void setName(String name) {
		this.name = name;
	}
	
	@RecordField(name="event_name")
	public String getName() {
		return name;
	}

	@RecordField(name="event_time")
	public void setTime(String time) {
		this.time = time;
	}
	
	@RecordField(name="event_time")
	public String getTime() {
		return time;
	}
	
	public void copyParams(Event event) {
		putAllExceptNull(event.getEventParams());
	}
	
	public void copyParams(Record record) {
		putAllExceptNull(record);
	}
	
	public Record getEventParams() {
		Record params = new Record();
		for (String field : getFields()) {
			if (!REF_EVENT.has(field))
				params.put(field, get(field));
		}
		return params;
	}
	
	public boolean triggers(String trigger) {
		NameFilter nptrigger = NameFilter.compile(trigger);
		return (nptrigger.accepts(name));
	}

	/*
	public String toXmlString() {
		return EventMarshaller.marshalToString(this);
	}

	public byte[] toXmlBytes() {
		return toXmlString().getBytes();
	}
	*/
	
	@Override
	public String toString() {
		return name + " " + super.toString();
	}
	
}
