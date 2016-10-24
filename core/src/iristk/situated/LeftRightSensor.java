package iristk.situated;

import iristk.util.Record;

public class LeftRightSensor extends Sensor {

	public static final int LEFT = -1;
	public static final int RIGHT = 1;
	
	@RecordField(name="factor")
	public Integer factor;
		
	public LeftRightSensor(String id, int factor) {
		super(id);
		this.factor = factor;
	}
	
	public LeftRightSensor() {
	}
	
	@Override
	public double distance(Agent system, Agent user, Record params) {
		// Assumes that the RIGHT sensor (Red singstar) is on the right side from the agent's perspective
		return system.getRelative(user.getHeadLocation()).x * factor;
	}
	
	
}
