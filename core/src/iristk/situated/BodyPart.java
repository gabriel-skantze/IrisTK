package iristk.situated;

import iristk.util.Record;

public class BodyPart extends Record {

	@RecordField(name="location")
	public Location location;
	
	@RecordField(name="rotation")
	public Rotation rotation;
	
	public BodyPart(float x, float y, float z) {
		this(new Location(x, y, z));
	}

	public BodyPart(Location location) {
		//this.rotation = new Rotation(0, 0, 0);
		this.location = location;
	}
	
	public BodyPart() {
		//this.rotation = new Rotation(0, 0, 0);
		this.location = new Location(0, 0, 0);
	}

		
}
