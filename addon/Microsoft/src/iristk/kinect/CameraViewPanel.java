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
import iristk.vision.CameraViewDecorator;

import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;


public class CameraViewPanel extends JPanel implements ColorFrameListener {

	protected CameraImage image = null;
	private boolean imageUpdated = false;
	private int height;
	private int width;
	private boolean imageDrawn = true;
	private CameraViewDecorator decorator = null;

	public CameraViewPanel(IrisGUI gui, IKinect kinect) {
		kinect.addColorFrameListener(this);
		gui.addDockPanel("kinect-color", "Kinect Color", this, true);
		new RepaintThread();
	}

	public CameraViewPanel(IKinect kinect) {
		kinect.addColorFrameListener(this);
	}
	
	public void setDecorator(CameraViewDecorator decorator) {
		this.decorator = decorator;
	}

	@Override
	public synchronized void onColorFrameReady(long ptr, int owidth, int oheight, int format) {
		try {
			if (getWidth() == 0 || getHeight() == 0)
				return;
			if (!imageDrawn)
				return;

			imageDrawn = false;
			if (image == null) {
				updateScale(owidth, oheight);
				image = new CameraImage(width, height);
			} else {
				if (updateScale(owidth, oheight)) {
					//System.out.println("New image");
					image = new CameraImage(width, height);
				}
			}
			//long t = System.currentTimeMillis();
			image.update(ptr, owidth, oheight, format);
			//System.out.println("A: " + (System.currentTimeMillis() - t));
			//this.format = format;
			this.imageUpdated  = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public CameraImage getColorImage() {
		return image;
	}

	/*
	private int c = 0;

	@Override
	public synchronized void onColorFrameReady(byte[] frame, int width, int height, int format) {

		if (image == null)
			image = new ColorImage(width, height);
		if (this.frame == null) {
			this.frame = new byte[frame.length];
		}
		System.arraycopy(frame, 0, this.frame, 0, frame.length);
		this.format = format;
		this.frameChanged  = true;
		c++;
		if (c % 100 == 0) {
		///	System.gc();
		//	System.out.println("Garbage collect");
		}

	}
	 */

	private class RepaintThread extends Thread {
		
		public RepaintThread() {
			start();
		}
		
		@Override
		public void run() {
			while (true) {
				if (imageUpdated) {
					repaint();
					imageUpdated = false;
				}
				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private boolean updateScale(int owidth, int oheight) {
		int nheight = Math.min(getWidth() * oheight / owidth, Math.max(40, getHeight()));
		int nwidth = Math.min(nheight * owidth / oheight, Math.max(40, getWidth()));

		if (nwidth > owidth) {
			nwidth = owidth;
			nheight = oheight;
		} else {
			nheight = nwidth * oheight / owidth;
		}
		
		if (height != nheight) {
			height = nheight;
			width = nwidth;
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected synchronized void paintComponent(Graphics g) {
		try {
			super.paintComponent(g);

			Graphics2D g2 = (Graphics2D)g;

			if (image != null) {

				//long t = System.currentTimeMillis();
				g.drawImage(image, 0, 0, null);
				//System.out.println("B: " + (System.currentTimeMillis() - t));

				if (decorator != null)
					decorator.decorate(g2, width, height);

				imageDrawn  = true;

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
