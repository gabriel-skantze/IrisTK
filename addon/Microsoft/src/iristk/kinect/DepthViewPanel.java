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
package iristk.kinect;

import iristk.net.kinect.*;
import iristk.system.IrisGUI;

import java.awt.Graphics;
import java.util.Random;

import javax.swing.JPanel;

public class DepthViewPanel extends JPanel implements DepthFrameListener {

	DepthImage image = new DepthImage();
	
	Random rand = new Random();
	
	public DepthViewPanel(IrisGUI gui, IKinect kinectVision) {
		kinectVision.addDepthFrameListener(this);
		gui.addDockPanel("kinect-depth", "Kinect Depth", this, true);
	}

	int frameCount = 0;
	
	@Override
	public synchronized void onDepthFrameReady(short[] frame) {
		image.update(frame);
		repaint();
	}
	
	@Override
	protected synchronized void paintComponent(Graphics g) {
		super.paintComponent(g);
		int height = Math.min(getWidth() * 3 / 4, getHeight());
		int width = Math.min(height * 4 / 3, getWidth());
		height = width * 3 / 4;
		g.drawImage(image, 0, 0, width, height, null);
	}

	
}
