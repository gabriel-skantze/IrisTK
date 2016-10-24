package iristk.situated;

import iristk.situated.SituationPanel;
import iristk.util.Record;

import java.awt.Color;

public abstract class Space extends Record {
	
	public abstract boolean contains(Location location);
	public abstract void draw(SituationPanel panel, int view);
	public abstract Location getCenter();

	public static class Sphere extends Space {
		@RecordField
		public Location center;
		@RecordField
		public double radius;

		public Sphere() {
		}
		
		public Sphere(Location center, double radius) {
			this.center = center;
			this.radius = radius;
		}
		
		@Override
		public boolean contains(Location location) {
			return (location.distance(center) <= radius);
		}
		
		@Override
		public void draw(SituationPanel panel, int view) {
			panel.drawOval(new Color(0.5f, 0.5f, 0.5f, 0.2f), null, -radius, -radius, radius * 2, radius * 2);
		}

		@Override
		public Location getCenter() {
			return center;
		}
	}
	
	
}
