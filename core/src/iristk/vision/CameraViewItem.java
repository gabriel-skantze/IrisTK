package iristk.vision;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

import iristk.util.Record;

public class CameraViewItem extends Record {

	public static final String SHAPE_RECT = "rect";
	public static final String SHAPE_OVAL = "oval";

	@RecordField
	public float x;
	
	@RecordField
	public float y;
	
	@RecordField
	public float width;
	
	@RecordField
	public float height;
	
	@RecordField
	public Record info;
	
	@RecordField
	public int color;
	
	@RecordField
	public String shape;
	
	@RecordField
	public float stroke;
	
	public void decorate(Graphics2D g, int w, int h) {
		g.setColor(new Color(color));
		g.setStroke(new BasicStroke(stroke));
		if (shape.equals(SHAPE_OVAL)) {
			//System.out.println((int)(w * width) + " " + (int)(h * height));
			g.drawOval((int)(x * w), (int)(y * h), (int)(w * width), (int)(h * height));
		} else if (shape.equals(SHAPE_RECT)) {
			g.drawRect((int)(x * w), (int)(y * h), (int)(w * width), (int)(h * height));
		}
	}
	
}
