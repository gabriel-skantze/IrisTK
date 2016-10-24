package iristk.util;

import java.awt.Component;

public class RepaintThread implements Runnable {

	private Thread thread;
	private Component component;
	private boolean shouldRepaint = false;

	public RepaintThread(Component component) {
		this.component = component;
		thread = new Thread(this);
		thread.start();
	}

	@Override
	public void run() {
		try {
			while (true) {
				if (shouldRepaint) {
					shouldRepaint = false;
					component.repaint();
				}
				Thread.sleep(40);
			}
		} catch (InterruptedException e) {
		}
	}
	
	public void repaint() {
		shouldRepaint = true;
	}

}
