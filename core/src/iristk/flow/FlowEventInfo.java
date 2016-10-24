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
package iristk.flow;

import iristk.system.Event;
import iristk.xml.XmlMarshaller.XMLLocation;

public class FlowEventInfo {

	private final State triggeringState;
	private final Event triggeringEvent;
	private final XMLLocation xmlLocation;

	public FlowEventInfo(State triggeringState, Event triggeringEvent) {
		this.triggeringState = triggeringState;
		this.triggeringEvent = triggeringEvent;
		this.xmlLocation = null;
	}

	public FlowEventInfo(State triggeringState, Event triggeringEvent, XMLLocation xmlLocation) {
		this.triggeringState = triggeringState;
		this.triggeringEvent = triggeringEvent;
		this.xmlLocation = xmlLocation;
	}
	
	public State getTriggeringState() {
		return triggeringState;
	}

	public Event getTriggeringEvent() {
		return triggeringEvent;
	}

	public XMLLocation getXmlLocation() {
		return xmlLocation;
	}

}
