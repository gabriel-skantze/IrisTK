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
package iristk.situated;

public class Agent extends Body {

	public static final String NOBODY = "nobody";
	public static final String ALL = "all";
	public static final String UNKNOWN = "unknown";

	@RecordField
	public String attending = UNKNOWN;
	
	@RecordField
	public boolean speaking = false;
	
	public Agent(String id) {
		super(id);
	}
	
	public Agent() {
	}
	
	public void setAttending(String target) {
		attending = target;
	}
	
	public void setAttendingAll() {
		attending = ALL;
	}

	public boolean isOnlyAttending(String target) {
		return attending.equals(target);
	}
	
	public boolean isAttending(String target) {
		return isAttendingAll() || attending.equals(target);
	}
	
	public boolean isAttendingAll() {
		return attending.equals(ALL);
	}

	public boolean isAttendingUnknown() {
		return attending.equals(UNKNOWN);
	}
	
	public boolean isAttendingNobody() {
		return attending.equals(NOBODY);
	}
	
	public boolean isSpeaking() {
		return speaking;
	}

	public boolean isNobody() {
		return id.equals(NOBODY);
	}
	
	public boolean isHuman() {
		return true;
	}

	/*
	public Situation getSituation() {
		return situation;
	}
	*/
		
}
