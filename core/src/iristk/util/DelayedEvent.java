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

public abstract class DelayedEvent implements Runnable {

	private Thread thread;
	private boolean forgotten = false;
	
	public DelayedEvent(final int delay) {
		thread = new Thread("DelayedEvent") {
			@Override
			public void run() {
				if (delay > 0) {
					try {
						Thread.sleep(delay);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				if (!forgotten)
					DelayedEvent.this.run();
			}
		};
		thread.start();
	}
	
	public void forget() {
		forgotten = true;
	}
	
}
