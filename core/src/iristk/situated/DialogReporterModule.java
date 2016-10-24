package iristk.situated;

import java.util.ArrayList;
import java.util.List;

import iristk.system.Event;
import iristk.system.InitializationException;
import iristk.system.IrisModule;
import iristk.util.Record;

public class DialogReporterModule extends IrisModule {

	private List<Listener> listeners = new ArrayList<>();
	private Record agents = new Record();
	
	@Override
	public void onEvent(Event event) {
		if (event.triggers("monitor.speech.start")) {
			reportUtterance(event.getString("agent", "system"), event.getString("text"));
		}
		if (event.triggers("sense.speech.rec")) {
			String text = event.getString("text");
			String agent = agents.getString(key(event), "user");
			reportUtterance(agent, text);
		}
		if (event.triggers("sense.user.speech.start")) {
			agents.put(key(event), event.getString("user"));
		}
	}
	
	private String key(Event event) {
		return event.getString("action", "") + "-" + event.getString("sensor", "");
	}

	private void reportUtterance(String agent, String utterance) {
		for (Listener listener : listeners) {
			listener.dialogUtterance(agent, utterance);
		}
	}

	public void addListener(Listener listener) {
		listeners.add(listener);
	}
	
	public static interface Listener {

		void dialogUtterance(String agent, String utterance);
		
	}

	@Override
	public void init() throws InitializationException {
	}
	
}
