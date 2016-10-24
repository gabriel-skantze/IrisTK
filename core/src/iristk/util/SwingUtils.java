package iristk.util;

import java.awt.Graphics;

public class SwingUtils {

	public static void drawString(Graphics g2d, String text, int x, int y) {
		for (String line : text.split("\n"))
			g2d.drawString(line, x, y += g2d.getFontMetrics().getHeight());
	}

	
}
