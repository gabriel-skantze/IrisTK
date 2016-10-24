package iristk.vision;

import java.awt.Color;

import iristk.situated.Sensor;
import iristk.situated.SituationPanel;
import iristk.util.ColorGenerator;

public class CameraSensor extends Sensor {

	@RecordField
	public float hfov;
	@RecordField
	public float vfov;
	
	@Override
	public void draw(SituationPanel panel, int view) {
		if (view == SituationPanel.TOPVIEW) {
			
			double backWidth = 0.22;
			double frontWidth = 0.28;
			double depth = 0.06;

			panel.drawPolygon(ColorGenerator.getColor(id), Color.BLACK, new double[]{-backWidth/2, -depth, backWidth/2, -depth, frontWidth/2, 0, -frontWidth/2, 0});
			double fov = Math.tan(Math.toRadians(hfov) / 2);
			panel.drawPolygon(new Color(0f, 1f, 0f, 0.1f), null, new double[]{0, 0, 10 * fov, 10, -10 * fov, 10});   
			
		} else if (view == SituationPanel.SIDEVIEW) {

			double height = 0.035;
			double depth = 0.06;
			
			panel.drawPolygon(ColorGenerator.getColor(id), Color.BLACK, new double[]{0, height/2, -depth, height/2, -depth, -height/2, 0, -height/2});
			double fov = Math.tan(Math.toRadians(vfov) / 2);
			panel.drawPolygon(new Color(0f, 1f, 0f, 0.1f), null, new double[]{0, 0, 10, -10 * fov, 10, 10 * fov});
		} else if (view == SituationPanel.FRONTVIEW) {

			double height = 0.035;
			double width = 0.28;
			
			panel.drawPolygon(ColorGenerator.getColor(id), Color.BLACK, new double[]{-width/2, height/2, width/2, height/2, width/2, -height/2, -width/2, -height/2});

		}
		
	}
	
}
