package iristk.video;

import java.awt.Graphics;
import java.awt.Image;
import javax.swing.JPanel;

public class VideoPanel extends JPanel implements VideoImageListener {

	private Image image;

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (image != null) {
			g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
		}
	}
	
	@Override
	public void newVideoImage(Image image) {
		this.image = image;
		repaint();
	}

}
