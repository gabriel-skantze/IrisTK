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

public interface FlowListener {

	void onFlowEvent(Event event, FlowEventInfo info);

	void onSendEvent(Event event, FlowEventInfo info);
	
	void onGotoState(State toState, FlowEventInfo info);
	
	void onCallState(State toState, FlowEventInfo info);
	
	void onReturnState(State fromState, State toState, FlowEventInfo flowEventInfo);

	void onFlowException(FlowException e);

}
