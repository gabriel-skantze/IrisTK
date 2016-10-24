package iristk.kinect;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class DepthImage extends BufferedImage {
	
	private int[] pixels;

	private static final int MIN_DEPTH = 800;
	private static final int MAX_DEPTH = 4096;
	
	public DepthImage() {
		super(320, 240, BufferedImage.TYPE_INT_RGB);
		pixels = ((DataBufferInt) getRaster().getDataBuffer()).getData();
	}
	
	public void update(short[] frame) {
		//for (int c = 0; c < 3; c++) {
			for (int x = 0; x < 320; x++) {
				for (int y = 0; y < 240; y++) {
					int val = frame[y * 320 + x];
					int depth = val >> 3;
					int v = 255;
					if (depth > MIN_DEPTH) {
						v = 255 - ((255 * (depth - MIN_DEPTH)) / (MAX_DEPTH - MIN_DEPTH));
					} else if (depth > MAX_DEPTH) {
						v = 0;
					}
					int pos = (y * 320 + x);
					int r = ((val & 1) == 0) ? v * 65536 : 0;
					int g = ((val & 2) == 0) ? v * 256 : 0;
					int b = ((val & 4) == 0) ? v : 0;
					pixels[pos] = r + g + b;
					/*
					int pos = (y * 320 + x) * 3 + c;
					if ((c == 0 && (val & 1) == 0) || (c == 1 && (val & 2) == 0) || (c == 2 && (val & 4) == 0))
						pixels[pos] = (byte) v;
					else
						pixels[pos] = 0;
						*/
				}
			}
		//}
			
	}
	
}
