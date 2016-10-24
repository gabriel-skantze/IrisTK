package iristk.kinect;

import iristk.situated.Agent;
import iristk.situated.Location;
import iristk.situated.Rotation;
import iristk.situated.SituationPanel;
import iristk.system.Event;
import iristk.util.Record;
import iristk.vision.CameraSensor;

public class KinectSensor extends CameraSensor {

	private Float angle = null;

	public KinectSensor(IKinect kinect) {
		this.id = kinect.getId();
		this.hfov = kinect.getHorizontalFOV();
		this.vfov = kinect.getVerticalFOV();
	}
	
	public KinectSensor() {
	}

	@Override
	public void draw(SituationPanel panel, int view) {
		super.draw(panel, view);
		if (view == SituationPanel.TOPVIEW) {
			if (angle != null) {
				Location micTarget = new Location(0, 0, 2).rotate(new Rotation(0, angle, 0));
				panel.drawLine(0, 0, micTarget.x, micTarget.z);
			}
		}
	}
	
	@Override
	public double distance(Agent system, Agent user, Record params) {
		Float yrotSensor = params.getFloat("angle");
		// Adjust for the fact that the microphone has an offset compared to the camera
		//Location micLocation = this.location.add(new Location(micoffset, 0, 0).rotate(this.rotation));
		Location micLocation = this.location;
		if (yrotSensor != null) {
			yrotSensor = (float) (yrotSensor + rotation.y);
			double yrotAgent = user.head.location.subtract(micLocation).toRotation().y;
			double diff = Math.abs(Rotation.angleDiff(yrotSensor, yrotAgent));
			//System.out.println(agent.id + " agent:" + yrotAgent + " sensor:" + yrotSensor + " diff:" + diff);
			return diff;
		} else
			return 0;
	}
	
	@Override
	public boolean onEvent(Event event) {
		if (event.triggers("sense.speech.start") && id.equals(event.getString("sensor", ""))) {
			Float angle = event.getFloat("angle");
			if (angle != null) {
				this.angle = angle;
				return true;
			}
		} else if (event.triggers("sense.speech.end") && id.equals(event.getString("sensor", ""))) {
			this.angle = null;
			return true;
		}
		return false;
	}
	
}
