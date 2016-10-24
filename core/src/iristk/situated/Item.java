package iristk.situated;

import iristk.situated.SituationPanel;
import iristk.util.Record;

public class Item extends Record {

	@RecordField
	public String id;
	@RecordField
	public Location location = new Location(0,0,0);
	@RecordField
	public Rotation rotation = new Rotation(0,0,0);
	@RecordField
	public Integer expire;
	@RecordField
	public Shape shape;
	
	public Item() {
	}
	
	public Item(String id) {
		this.id = id;
	}

	public void draw(SituationPanel panel, int view) {
		//panel.drawOval(ColorGenerator.getColor(id), Color.BLACK, -0.05, -0.05, 0.1, 0.1);
		if (shape != null) {
			shape.draw(panel, view);
		}
	}
	
	public Record getPosition() {
		Record position = new Record();
		if (location != null)
			position.put("location", location);
		if (rotation != null)
			position.put("rotation", rotation);
		return position;
	}
	
}
