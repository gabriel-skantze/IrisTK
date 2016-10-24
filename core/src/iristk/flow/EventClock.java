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
package iristk.flow;

import iristk.flow.FlowRunner.FlowThread;
import iristk.system.Event;

import java.util.Random;

public class EventClock {

	Random rand = new Random();
	private int minInt;
	private int maxInt;
	private String eventName;
	private FlowThread flowThread;
	private boolean running;
	private ClockThread clockThread;
	
	public EventClock(FlowThread flowThread, int minInt, int maxInt, String event) {
		this.minInt = minInt;
		this.maxInt = maxInt;
		this.eventName = event;
		this.flowThread = flowThread;
		this.clockThread = new ClockThread();
		clockThread.start();
	}
		
	private class ClockThread extends Thread {
		@Override
		public void run() {
			running = true;
			while (running) {
				int interval;
				if (minInt == maxInt)
					interval = minInt;
				else
					interval = rand.nextInt(1 + maxInt - minInt) + minInt;
				try {
					Thread.sleep(interval);
				} catch (InterruptedException e) {
					break;
				}
				if (running && flowThread.isRunning()) {
					Event event = new Event(eventName);
					flowThread.addEvent(event);
				}
			}
		}
	}
	
	public void stop() {
		running = false;
	}
	
}
