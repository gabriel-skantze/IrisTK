package iristk.situated;

import java.awt.Color;

import iristk.util.Record;

public abstract class Shape extends Record {

	public void draw(SituationPanel panel, int view) {
	}

	public static class Box extends Shape {
		
		@RecordField
		public double xsize;
		@RecordField
		public double ysize;
		@RecordField
		public double zsize;
		
		public Box() {
		}
		
		public Box(double xsize, double ysize, double zsize) {
			this.xsize = xsize;
			this.ysize = ysize;
			this.zsize = zsize;
		}
		
		@Override
		public void draw(SituationPanel panel, int view) {
			if (view == SituationPanel.TOPVIEW) {
				if (xsize > 0 & zsize > 0)
					panel.drawPolygon(null, Color.BLACK, new double[]{-xsize/2, -zsize/2, xsize/2, -zsize/2, xsize/2, zsize/2, -xsize/2, zsize/2});
			}
			if (view == SituationPanel.SIDEVIEW) {
				if (zsize > 0 & ysize > 0)
					panel.drawPolygon(null, Color.BLACK, new double[]{-zsize/2, -ysize/2, zsize/2, -ysize/2, zsize/2, ysize/2, -zsize/2, ysize/2});
			}
		}
		
		
	}
	
	public static void main(String[] args) throws Exception {
		System.out.println(Record.fromJSON(new Shape.Box(1, 1, 1).toJSON().toString()));
	}
	
}
