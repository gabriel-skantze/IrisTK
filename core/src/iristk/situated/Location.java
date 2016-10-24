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

public class Location extends Record {

	@RecordField(name="x")
	public double x;
	@RecordField(name="y")
	public double y;
	@RecordField(name="z")
	public double z;
	
	public Location() {
	}
	
	public Location(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Location(Location location) {
		this.x = location.x;
		this.y = location.y;
		this.z = location.z;
	}

	public Location(Record record) {
		this.x = record.getFloat("x");
		this.y = record.getFloat("y");
		this.z = record.getFloat("z");
	}

	public Location scale(double s) {
		return new Location(this.x * s, this.y * s, this.z * s);
	}

	public Location add(Location location) {
		return new Location(this.x + location.x, this.y + location.y, this.z + location.z);
	}
	
	public static Location sum(Location... locations) {
		Location result = new Location(0, 0, 0);
		for (Location loc : locations) {
			result = result.add(loc);
		}
		return result;
	}
	
	public Location subtract(Location l2) {
		return new Location(this.x - l2.x, this.y - l2.y, this.z - l2.z);
	}

	public Location multiply(Location l2) {
		return new Location(this.x * l2.x, this.y * l2.y, this.z * l2.z);
	}
	
	public static Location mean(Location... locations) {
		return sum(locations).scale(1.0 / locations.length);
	}
	
	public double distance(Location location) {
		return Math.sqrt(Math.pow(this.x - location.x, 2) + Math.pow(this.y - location.y, 2) + Math.pow(this.z - location.z, 2));
	}

	public static double dot(Location u, Location v) 
	{
		return u.x * v.x + u.y * v.y + u.z * v.z;   
	}
	
	/**
	 * Calculates the point of intersection between a line that goes through r1 and r1b, and one that goes through r2 and r2b
	 * The algorithm doesn't require that the lines actually intersect, if not it will return the point where the lines are closest to each other. 
	 * 
	 * @return null if the lines are parallel
	 */
	public static Location intersection(Location r1, Location r1b, Location r2, Location r2b) {
		// http://stackoverflow.com/questions/10551555/need-an-algorithm-for-3d-vectors-intersection
		
		//Given two lines passing through 3D points r1=[r1x,r1y,r1z] and r2=[r2x,r2y,r2z] 
		// and having unit directions e1=[e1x,e1y,e1z] and e2=[e2x,e2y,e2z] 
		Location e1 = r1b.subtract(r1).scale(1 / r1.distance(r1b));
		Location e2 = r2b.subtract(r2).scale(1 / r2.distance(r2b));
		
		//Find the direction projection u=Dot(e1,e2)=e1x*e2x+e1y*e2y+e1z*e2z
		double u = dot(e1, e2);
		
		//If u==1 then lines are parallel. No intersection exists.
		if (u == 1)	return null;
		
		//Find the separation projections t1=Dot(r2-r1,e1) and t2=Dot(r2-r1,e2)
		double t1 = dot(r2.subtract(r1), e1);
		double t2 = dot(r2.subtract(r1), e2);
		
		//Find distance along line1 d1 = (t1-u*t2)/(1-u*u)
		double d1 = (t1 - u * t2) / (1 - u * u);
		//Find distance along line2 d2 = (t2-u*t1)/(u*u-1)
		double d2 = (t2 - u * t1) / (u * u - 1);
		
		//Find the point on line1 p1=Add(r1,Scale(d1,e1))
		Location p1 = r1.add(e1.scale(d1));
		
		//Find the point on line2 p2=Add(r2,Scale(d2,e2))
		Location p2 = r2.add(e2.scale(d2));
		
		return mean(p1, p2);
	}
	

	public static Location intersection(Location l1, Rotation r1, Location l2, Rotation r2) {
		Location l1b = l1.add(new Location(0, 0, 1).rotate(r1));
		Location l2b = l2.add(new Location(0, 0, 1).rotate(r2));
		return intersection(l1, l1b, l2, l2b);
	}
	
	public Location rotate(Rotation rotation)
	{
		/* 3D Rotation matrix 
		 * 
		 *  R(alpha,beta,gamma) = Rz (alpha) Ry (beta) Rx (gamma)
		 * 
		 *  | [cos_a * cos_b]   [cos_a * sin_b * sin_g - sin_a * cos_g]     [cos_a * sin_b * cos_g + sin_a * sin_g] |
		 *  | [sin_a * cos_b]   [sin_a * sin_b * sin_g + cos_a * cos_g]     [sin_a * sin_b * cos_g - cos_a * sin_g] |    
		 *  | [-sin_b       ]   [cos_b * sin_g                        ]     [cos_b * cos_g                        ] |
		 * 
		 */

		double sin_a = Math.sin(Math.toRadians(rotation.z));
		double sin_b = Math.sin(Math.toRadians(rotation.y));
		double sin_g = Math.sin(Math.toRadians(rotation.x));  

		double cos_a = Math.cos(Math.toRadians(rotation.z));
		double cos_b = Math.cos(Math.toRadians(rotation.y));
		double cos_g = Math.cos(Math.toRadians(rotation.x));                                                                                                              

		double new_x_calc_1 = cos_a * cos_b * x; 
		double new_x_calc_2 = ((cos_a * sin_b * sin_g) - (sin_a * cos_g)) * y; 
		double new_x_calc_3 = ((cos_a * sin_b * cos_g) + (sin_a * sin_g)) * z; 
		double new_y_calc_1 = sin_a * cos_b * x; 
		double new_y_calc_2 = ((sin_a * sin_b * sin_g) + (cos_a * cos_g)) * y; 
		double new_y_calc_3 = ((sin_a * sin_b * cos_g) - (cos_a * sin_g)) * z; 
		double new_z_calc_1 = -sin_b * x; 
		double new_z_calc_2 = cos_b * sin_g * y; 
		double new_z_calc_3 = cos_b * cos_g * z;         

		double xn = new_x_calc_1 + new_x_calc_2 + new_x_calc_3;
		double yn = new_y_calc_1 + new_y_calc_2 + new_y_calc_3;
		double zn = new_z_calc_1 + new_z_calc_2 + new_z_calc_3;
		
		return new Location(xn, yn, zn);
	}
	
	/**
	 * (0, 1) = 0
	 * (-1, 0) = 90
	 * (0, -1) = 180
	 * (1, 0) = 270
	 */
	private static double angle(double x, double y) {
		double angle = 0;
		if (y > 0) 
			angle = Math.toDegrees(Math.atan(x / y));
		else if (y < 0)
			angle = 180 - Math.toDegrees(Math.atan(-x / y));
		else if (x > 0)
			angle = 90;
		else if (x < 0)
			angle = 270;
		return (angle + 360) % 360;
	}
	
	// This calculates which rotation would be needed to move this location to 0,0,1
	/*
	public Rotation toRotation2() {
		double xrot = angle(-y, Math.abs(z));
		Location loc2 = rotate(new Rotation(-xrot, 0, 0));
		double yrot = angle(loc2.x, loc2.z);
		return new Rotation(xrot, yrot, 0);
	}
	
	public Rotation toRotationOld() {
		return new Rotation(angle(-y, Math.abs(z)), angle(x, z), 0);
	}
	*/
	
	// This calculates which rotation would be needed to move a vector at [0,0,1] to this location 
	public Rotation toRotation() {
		double yrot = angle(x, z);
		Location loc2 = this.rotate(new Rotation(0, -yrot, 0));
		double xrot = angle(-loc2.y, Math.abs(loc2.z));
		return new Rotation(xrot, yrot, 0);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Location) {
			Location loc = (Location)obj;
			return x == loc.x && y == loc.y && z == loc.z;
		} else {
			return super.equals(obj);
		}
	}
		
	public static void main(String[] args) {
		//System.out.println(new Location(1,1,1).toRotation3());
		
		/*
		for (double x=-3; x <= 3; x += 0.5) {
			for (double y=-3; y <= 3; y += 0.5) {
				Location loc = new Location(1, x, y);
				Rotation rot = loc.toRotation();
				Rotation rot2 = loc.toRotation3();
				if (Math.abs(rot.x - rot2.x) > 35)
					System.out.println(loc + " " + rot + " " + rot2);
			}
		}
		*/
	}


	
}
