package iristk.situated;

import iristk.system.Event;
import iristk.system.InitializationException;
import iristk.system.IrisModule;
import iristk.util.Record;

public abstract class SensorModule extends IrisModule {

	private Sensor sensor;
	private boolean primary;
	
	@Override
	public void init() throws InitializationException {
		sendSensorDetect();
	}
	
	@Override
	public void onEvent(Event event) {
		if (event.triggers("action.situation.detect")) {
			sendSensorDetect();
		} else if (event.triggers("sense.situation")) {
			if (sensor != null) {
				Record data = event.getRecord(this.sensor.id);
				if (data != null) {
					this.sensor.putAllExceptNull(data);
				}
			}
		}
	}
	
	public Sensor getSensor() {
		return sensor;
	}

	public void setSensor(Sensor sensor, boolean primary) {
		this.sensor = sensor;
		this.primary = primary;
	}

	protected void sendSensorDetect() {
		if (getSystem() != null && sensor != null && primary) {
			//TODO: should we remove position data if hasPosition() is false?
			Event event = new Event("sense.situation");
			event.put(sensor.id, sensor);
			//System.out.println(event);
			send(event);
		}
	}

	public void setPosition(Location location, Rotation rotation) {
		if (sensor != null && sensor.hasPosition()) {
			sensor.location = location;
			sensor.rotation = rotation;
			sendSensorDetect();
		}
	}
	
	@Override
	public String getUniqueName() {
		if (sensor != null && primary) {
			return sensor.id;
		} else {
			return null;
		}
	}
	
	@Override
	public void stop() {
		Event event = new Event("sense.situation");
		sensor.expire = 0;
		event.put(sensor.id, sensor);
		send(event);
		super.stop();
	}
	
}
