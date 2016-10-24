/*******************************************************************************
 * Copyright (c) 2014 Gabriel Skantze.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Gabriel Skantze - initial API and implementation
 ******************************************************************************/
package iristk.situated;

import iristk.util.Record;

public class Rotation extends Record {
	
	// Tilt
	@RecordField(name="x")
	public double x;
	// Yaw
	@RecordField(name="y")
	public double y;
	// Roll
	@RecordField(name="z")
	public double z;
	
	public Rotation() {
	}
	
	public Rotation(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Rotation(Rotation rot) {
		this.x = rot.x;
		this.y = rot.y;
		this.z = rot.z;
	}

	public Rotation(Record record) {
		this.x = record.getFloat("x");
		this.y = record.getFloat("y");
		this.z = record.getFloat("z");
	}
	
	public Rotation add(Rotation r) {
		Rotation rot = new Rotation(this.x + r.x, this.y+ r.y, this.z + r.z);
		rot.normalize();
		return rot;
	}

	public Rotation subtract(Rotation r) {
		Rotation rot = new Rotation(this.x - r.x, this.y - r.y, this.z - r.z);
		rot.normalize();
		return rot;
	}
	
	public Rotation scale(double s) {
		Rotation rot = new Rotation(x * s, y * s, z * s);
		rot.normalize();
		return rot;
	}
	
	private static double mean(double a, double b, int w) {
		if (Math.abs(a - b) < Math.abs((a + 360) - b) && Math.abs(a - b) < Math.abs((b + 360) - a)) {
			return (a + (b * w)) / (w + 1);
		} else if (Math.abs((a + 360) - b) < Math.abs(a - b) && Math.abs((a + 360) - b) < Math.abs((b + 360) - a)) {
			return ((a + 360) + (b * w)) / (w + 1);
		} else {
			return (a + ((b + 360) * w)) / (w + 1);
		}
	}
	
	public Rotation mean(Rotation rotation, int w) {
		Rotation rot = new Rotation(mean(x, rotation.x, w), mean(y, rotation.y, w), mean(z, rotation.z, w));
		rot.normalize();
		return rot;
	}
	
	public static Rotation mean(Rotation... rotations) {
		Rotation mean = null;
		int w = 1;
		for (Rotation rot : rotations) {
			if (mean == null) {
				mean = rot;
			} else {
				mean = rot.mean(mean, w);
				w++;
			}
		}
		return mean;
	}
	
	public static void main(String[] args) {
		Location userLocation = new Location(-0.5, 0.3, 1);
		Rotation userRotation = new Rotation(0, 0, 0);
		
		Location furhatLocation = new Location(0, 0.45, -0.55);
		Location kinectLocation = new Location(0, 0.16, -0.37);
		Rotation kinectRotation = new Rotation(353, 0, 0);
				
		userLocation = userLocation.rotate(kinectRotation).add(kinectLocation);
		
		userRotation = userRotation.add(new Rotation(-kinectRotation.x, kinectRotation.y + 180, kinectRotation.z));
		
		Rotation userFurhatRotation = furhatLocation.subtract(userLocation).toRotation();
		
		double xdiff = Rotation.angleDiff(userRotation.x, userFurhatRotation.x);
		double ydiff = Rotation.angleDiff(userRotation.y, userFurhatRotation.y);
	
	}
		
	private void normalize() {
		while (x < 0) x += 360;
		while (x >= 360) x -= 360;
		while (y < 0) y += 360;
		while (y >= 360) y -= 360;
		while (z < 0) z += 360;
		while (z >= 360) z -= 360;
	}
	
	public Location toLocation(double distance) {
		return new Location(0, 0, distance).rotate(this);
	}
	
	public double angleTo(Rotation rot) {
		double xdiff = angleDiff(x, rot.x);
		double ydiff = angleDiff(y, rot.y);
		return Math.sqrt(xdiff * xdiff + ydiff * ydiff);
	}
	
	public static double angleDiff(double a, double b) {
		if (Math.abs(a - b) < Math.abs((a + 360) - b) && Math.abs(a - b) < Math.abs((b + 360) - a)) {
			return a - b;
		} else if (Math.abs((a + 360) - b) < Math.abs(a - b) && Math.abs((a + 360) - b) < Math.abs((b + 360) - a)) {
			return (a + 360) - b;
		} else {
			return a - (b + 360);
		}
		//return Math.min(Math.min(Math.abs((angle1 - 360) - angle2), Math.abs((angle2 - 360) - angle1)), Math.abs(angle1 - angle2));
	}

	public Rotation invert() {
		Rotation rot = new Rotation(-x, -y, -z);
		rot.normalize();
		return rot;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Rotation) {
			Rotation rot = (Rotation)obj;
			return x == rot.x && y == rot.y && z == rot.z;
		} else {
			return super.equals(obj);
		}
	}

}

		