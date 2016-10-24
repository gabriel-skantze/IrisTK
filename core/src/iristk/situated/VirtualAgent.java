package iristk.situated;

import iristk.system.Event;
import iristk.system.InitializationException;
import iristk.system.IrisModule;

public class VirtualAgent extends IrisModule {
	
	private String agentName;
	
	public VirtualAgent(String name) {
		this.agentName = name;
	}

	@Override
	public void onEvent(Event event) {
		if (event.has("agent") && !event.getString("agent").equals(agentName)) 
			return;
		if (event.triggers("action.gesture")) {
			Event monitor = new Event("monitor.gesture.end");
			monitor.put("action", event.getId());
			send(monitor);
		} else if (event.triggers("action.gaze")) {
			Location targetGaze;
			if (event.has("location"))
				targetGaze = new Location(event.getRecord("location"));
			else
				targetGaze = new Location(event.getDouble("x"), event.getDouble("y"), event.getDouble("z"));
			Event monitor = new Event("monitor.gaze");
			monitor.put("action", event.getId());
			monitor.put("location", targetGaze);
			monitor.put("head:rotation", targetGaze.toRotation());
			if (event.has("agent"))
				monitor.put("agent", event.get("agent"));
			send(monitor);
		} 
	}

	@Override
	public void init() throws InitializationException {
	}

}
