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
package iristk.util;

import java.awt.Color;
import java.util.Random;

public class ColorGenerator {

	static Random random = new Random();
	
	public static Color getColor(int key) {
		random.setSeed(key);
		float hue = random.nextFloat();
		float saturation = (random.nextInt(2000) + 1000) / 10000f;
		float luminance = 0.9f;
		Color color = Color.getHSBColor(hue, saturation, luminance);
		return color;
	}
	
	public static Color getColor(String key) {
		return getColor(key.hashCode());	
	}
	
	public static Color getBrightColor(int key) {
		random.setSeed(key);
		float hue = random.nextFloat();
		float saturation = (random.nextInt(2000) + 7000) / 10000f;
		float luminance = 0.9f;
		Color color = Color.getHSBColor(hue, saturation, luminance);
		return color;
	}

	public static Color getBrightColor(String key) {
		return getBrightColor(key.hashCode());	
	}
	
	public static float getHue(Color color) {
		float[] hsbVals = new float[3];
		Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsbVals);
		return hsbVals[0];
	}
	
}
