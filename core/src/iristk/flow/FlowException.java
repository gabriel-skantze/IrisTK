package iristk.flow;

import iristk.system.Event;
import iristk.xml.XmlMarshaller.XMLLocation;

public class FlowException extends Exception {

	private FlowEventInfo flowEventInfo;

	public FlowException(Exception e, State state, Event event, XMLLocation xmlLocation) {
		super("Event '" + event.getName() + "' in state '" + state.getClass().getSimpleName() + "' at " + xmlLocation.getFile().getName() + ":" + xmlLocation.getLineNumber(), e);
		this.flowEventInfo = new FlowEventInfo(state, event, xmlLocation);
	}

	public FlowException(Exception e, State state, Event event) {
		super("Event '" + event.getName() + "' in state '" + state.getClass().getSimpleName() + "'", e);
		this.flowEventInfo = new FlowEventInfo(state, event);
	}

	public FlowEventInfo getFlowEventInfo() {
		return flowEventInfo;
	}
	
}
