package iristk.situated;

import iristk.system.Event;
import iristk.system.InitializationException;
import iristk.system.IrisModule;
import iristk.system.IrisSystem;
import iristk.util.Record;

import java.io.File;
import java.io.IOException;

public class SituationModule extends IrisModule {

	protected Situation situation;
	
	public SituationModule() {
		situation = new Situation();
	}

	@Override
	public void init() throws InitializationException {
		send(new Event("action.situation.detect"));
	}

	@Override
	public void onEvent(Event event) {
		situation.onEvent(event);
	}

	public void loadPositions(File file) {
		loadPositions(getSystem(), file);
	}
	
	public static void loadPositions(IrisSystem system, File file) {
		if (file.exists()) {
			try {
				Event event = new Event("sense.situation");
				Record sensorData = Record.fromProperties(file);
				event.putAllExceptNull(sensorData);
				system.send(event);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void savePositions(File file) {
		situation.savePositions(file);
	}
	
	public Situation getSituation() {
		return situation;
	}
	
}
