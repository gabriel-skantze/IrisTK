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
package iristk.system;

import iristk.util.NameFilter;

import java.util.concurrent.ArrayBlockingQueue;

import org.slf4j.Logger;

public abstract class IrisModule implements Runnable, EventListener {
	
	private Thread thread;
	String moduleName;
	IrisSystem system;
	ArrayBlockingQueue<Event> eventQueue = new ArrayBlockingQueue<Event>(1000);
	NameFilter subscribes = NameFilter.ALL;
	private boolean running = false;
	private boolean enabled = true;
	private boolean systemStarted = false;
	
	private Logger logger = IrisUtils.getLogger(getClass());
	
	private boolean eventQueueHealthy = true;
		
	public IrisModule() {
	}
	
	public void send(Event event) {
		if (system != null && enabled)
			system.send(event, getName());
	}
	
	public void monitorState(String... states) {
		if (system != null)
			system.monitorState(moduleName, states);
	}
	
	public void subscribe(NameFilter filter) {
		subscribes = filter;
	}
	
	public void subscribe(String filter) {
		subscribes = NameFilter.compile(filter).combine(NameFilter.compile("*.system.** *.module.**"));
	}
	
	public final void start() {
		pingModule(null);
		running = true;
		thread = new Thread(this);
		thread.start();
	}

	/*
	void sendStartedEvent() {
		Event event = new Event("monitor.module.start");
		event.put("system", system.getName());
		send(event);
	}
	*/
	
	public void stop() {
		running = false;
		thread.interrupt();
		try {
			thread.join();
		} catch (InterruptedException e) {
		}
	}
	
	public String getName() {
		return moduleName;
	}
	
	void setName(String name) {
		this.moduleName = name;
	}
	
	public IrisSystem getSystem() {
		return system;
	}
	
	public void setSystem(IrisSystem system) {
		this.system = system;
	}
	
	@Override
	public final void run() {
		while (running) {
			try {
				Event event = eventQueue.take();
				try {
					if (!systemStarted  && event.triggers("monitor.system.start") && getSystem().getName().equals(event.getString("system"))) {
						systemStarted = true;
						systemStarted();
					} else if (event.triggers("action.module.ping")) {
						pingModule(event.getId());
					}
					onEvent(event);
				} catch (Exception e) {
					logger.error("Problem processing event " + event.getName(), e);
				}
			} catch (InterruptedException e) {
				break;
			}
		}
	}
	
	public void pingModule(String action) {
		Event ping = new Event("monitor.module.ping");
		if (action != null)
			ping.put("action", action);
		ping.put("system", system.getName());
		send(ping);
	}
	
	protected void systemStarted() {
	}
	
	public boolean isRunning() {
		return running;
	}
	
	/*
	 * Adds an event to the module's eventQueue so that it is processed in the module's own thread.
	 */
	public void invokeEvent(Event message) {
		try {
			if (eventQueueHealthy)
				eventQueue.add(message);
		} catch (IllegalStateException e) {
			logger.error("Event queue full");
			eventQueueHealthy = false;
		}
	}
	
	public abstract void init() throws InitializationException;
	
	//public Record getConfig() {
	//	return getSystem().getConfig(moduleName);
	//}
		
	@Override
	public String toString() {
		return moduleName;
	}

	public String getDefaultName() {
		return getClass().getSimpleName();
	}

	public boolean isEnabled() {
		return enabled;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled  = enabled;
	}

	public String getUniqueName() {
		return null;
	}
	
	
}
