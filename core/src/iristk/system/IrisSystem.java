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

import iristk.project.Project;
import iristk.project.Package;
import iristk.util.NameFilter;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;

public class IrisSystem implements EventListener {
	
	private static Logger logger = IrisUtils.getLogger(IrisSystem.class);

	private ArrayList<IrisModule> modules = new ArrayList<IrisModule>();
	private ArrayList<BrokerClient> brokerClients = new ArrayList<>();

	private String systemName;

	static int sessionId = (int) (System.currentTimeMillis() / 60000) - (40 * 365 * 24 * 60);

	private long timestampOffset = 0;
	private ArrayList<EventListener> eventListeners = new ArrayList<EventListener>();
	private boolean running = false;
	private Integer brokerPort = 1932;
	private String brokerHost = "localhost";
	private ArrayBlockingQueue<Event> eventQueue = new ArrayBlockingQueue<Event>(1000);
	private Thread systemThread;
	private Class<?> packageClass;
	private File packageDir;

	private ArrayList<String> distributedModules = new ArrayList<>();
	private Map<String,HashSet<String>> pingMap = new HashMap<>();

	//private ErrorHandler errorHandler = new ErrorHandler() {
	//	@Override
	//	public void handle(Exception e, String component) {
	//		logger.error("Error in " + component, e);
	//	};
	//};
	private BrokerClient brokerClient;

	static int idCounter = 0;

	public IrisSystem(String name, Package pack) throws Exception {
		this(name, pack != null ? pack.getPath() : null);
	}

	public IrisSystem(Class<?> packageClass) throws Exception {
		this(packageClass.getSimpleName(), Project.main.getPackage(packageClass));
	}

	public IrisSystem(String name, File path) {
		this.systemName = name;
		if (path != null)
			this.packageDir = path;
		else
			this.packageDir = IrisUtils.getTempDir("system/" + name);
		logger.info("Java " + System.getProperty("java.version") + " " + System.getProperty("os.arch"));
		logger.info("Initializing system " + systemName);
		startAsync();
	}
	
	public IrisSystem(Package pack) throws Exception {
		this(pack.getName(), pack);
	}
	
	public IrisSystem(String name) throws Exception {
		this(name, (File)null);
	}
	
	//public IrisSystem(String name, Class<?> packageClass) throws Exception {
	//	this(name, Project.main.getPackage(packageClass).getPath());
	//}

//	public IrisSystem(Class<?> packageClass) throws Exception {
//		this(packageClass.getSimpleName(), packageClass);
//	}

	//public void setErrorHandler(ErrorHandler errorHandler) {
	//	this.errorHandler = errorHandler;
	//}


	public String getName() {
		return systemName;
	}

	public void addEventListener(EventListener listener) {
		eventListeners.add(listener);
	}

	public void addModule(IrisModule module) throws InitializationException {
		addModule(module.getDefaultName(), module);
	}

	public synchronized void addModule(String baseName, IrisModule module) throws InitializationException {
		if (module.getUniqueName() != null) {
			if (getModule(module.getUniqueName()) != null) {
				throw new InitializationException("Could not add module " + module.getUniqueName() + ": there is already such a module in the system");
			}
			module.setName(module.getUniqueName());
		} else {
			int i = 0;
			String name = baseName;	
			while (getModule(name) != null) {
				i++;
				name = baseName + "-" + i; 
			}
			module.setName(name);
		}
		module.setSystem(this);
		modules.add(module);
		logger.info("Initializing module " + module.getName());
		module.init();
		for (BrokerClient brokerClient : brokerClients) {
			updateSubscriptions(brokerClient);
		}
		module.start();
	}

	public synchronized IrisModule getModule(String name) {
		for (IrisModule module : modules) {
			if (module.getName().equals(name))
				return module;
		}
		return null;
	}

	@Override
	public void onEvent(Event event) {
		// An event from the broker
		distributeInternal(event);
	}

	public void connectToBroker(String ticket) throws IOException {
		brokerClient = new BrokerClient(ticket, systemName, brokerHost, brokerPort, this);
		brokerClient.connect();
		brokerClients.add(brokerClient);
		//for (IrisModule module : modules) {
		//	module.sendStartedEvent();
		//}
		updateSubscriptions(brokerClient);
		send(new Event("action.module.ping"));
	}

	public void connectToBroker(String ticket, String host) throws IOException {
		setBrokerHost(host);
		connectToBroker(ticket);
	}

	public void connectToBroker(String ticket, String host, int port) throws IOException {
		setBrokerHost(host);
		setBrokerPort(port);
		connectToBroker(ticket);
	}

	private void distributeExternal(Event event) {
		for (BrokerClient brokerClient : brokerClients) {
			try {
				brokerClient.send(event);
			} catch (IOException e) {
				logger.error("Problem sending to broker", e);
			}
		}
	}

	private synchronized void distributeInternal(Event event) {
		this.eventQueue.add(event);
		for (EventListener listener : eventListeners) {
			listener.onEvent(event);
		}
		for (IrisModule module : modules) {
			if (module.isEnabled() && module.subscribes.accepts(event.getName())) {
				module.invokeEvent(event);
			}
		}
	}

	private String generateEventId(String name) {
		return name + "." + IrisSystem.sessionId + "." + idCounter++;
	}

	public String getTimestamp() {
		return getTimestamp(System.currentTimeMillis());
	}

	public String getTimestamp(long time) {
		return new Timestamp(time - timestampOffset).toString();
	}

	public boolean isRunning() {
		return running;
	}

	public void send(Event event) {
		send(event, getName());
	}

	public void send(Event event, String sender) {
		if (event.getSender() == null)
			event.setSender(sender);
		if (event.getTime() == null)
			event.setTime(getTimestamp());
		if (event.getId() == null)
			event.setId(generateEventId(sender));
		distributeInternal(event);
		distributeExternal(event);
	}

	public void monitorState(String sender, String[] states) {
		Event monitorEvent = new Event("monitor.module.state");
		monitorEvent.put("states", Arrays.asList(states));
		send(monitorEvent, sender);
	}

	public void setBrokerHost(String host) {
		this.brokerHost = host;
	}

	public void setBrokerPort(Integer port) {
		this.brokerPort = port;
	}

	public void setTimestampOffset(long offset) {
		this.timestampOffset = offset;
	}

	private void startAsync() {
		systemThread = 
				new Thread() {
			@Override
			public void run() {
				running  = true;
				while (running) {
					try {
						Event event = eventQueue.poll(5, TimeUnit.MILLISECONDS);
						if (event != null) {
							if (event.triggers("action.system.stop")) {
								IrisSystem.this.stop();
							} else if (event.triggers("monitor.module.ping")) {
								String action = event.getString("action", "");
								if (pingMap.containsKey(action)) {
									pingMap.get(action).remove(event.getString("system") + ":" + event.getSender());
									//System.out.println(new ArrayList<>(pingMap.get(action)));
								}
								String name = event.getString("system") + ":" + event.getSender();
								if (!distributedModules.contains(name)) {
									distributedModules.add(name);
									//System.out.println(distributedModules);
								}
							} else if (event.triggers("monitor.system.disconnected")) {
								String dissys = event.getString("system", "");
								for (String name : new ArrayList<>(distributedModules)) {
									if (name.startsWith(dissys + ":"))
										distributedModules.remove(name);
								}
								//System.out.println(distributedModules);
							} else if (event.triggers("monitor.module.stop")) {
								removeDistributedModule(event.getString("system"), event.getString("module"));
							}
						}
					} catch (InterruptedException e) {
						break;
					}
				}
				for (IrisModule module : modules) {
					logger.info("Stopping module " + module.getName());
					module.stop();
				}
			};
		};
		systemThread.start();
	}

	protected void removeDistributedModule(String system, String module) {
		distributedModules.remove(system + ":" + module);
	}

	public void pingModules(int timeout) throws InitializationException {
		Event event = new Event("action.module.ping");
		String pingId = generateEventId(getName());
		event.setId(pingId);
		HashSet<String> pingModules = new HashSet<>();
		pingMap.put(pingId, pingModules);
		for (String mod : distributedModules) {
			pingModules.add(mod);
		}
		send(event);
		int i = 0;
		try {
			while (pingModules.size() > 0) {
				Thread.sleep(10);
				i++;
				if (i * 10 > timeout)
					throw new InitializationException("Not all modules were ready: " + new ArrayList<>(pingModules));
			}
		} catch (InterruptedException e) {
			throw new InitializationException(e.getMessage());
		} finally {
			pingMap.remove(pingId);
		}
	}

	public void sendStartSignal() throws InitializationException {
		pingModules(10000);
		Event event = new Event("monitor.system.start");
		event.put("system", systemName);
		send(event, systemName);
		logger.info("System started");
	}

	public void stop() {
		running = false;
		try {
			systemThread.join();
		} catch (InterruptedException e) {
		}
		if (brokerClient != null) {
			try {
				brokerClient.close();
			} catch (IOException e) {
			}
		}
		logger.info("System stopped");
	}

	public void updateSubscriptions(BrokerClient brokerClient) {
		NameFilter filter = NameFilter.NONE;
		for (IrisModule module : modules) {
			filter = filter.combine(module.subscribes);
		}
		try {
			brokerClient.subscribe(filter);
		} catch (IOException e) {
			logger.error("Problem subscribing to broker", e);
		}
	}

	public List<IrisModule> getModules() {
		return modules;
	}

	public File getPackageDir() {
		return packageDir;
	}
	
	public File getPackageFile(String path) {
		return new File(getPackageDir(), path);
	}

	public synchronized void removeModule(IrisModule module) {
		modules.remove(module);
		removeDistributedModule(module.getSystem().getName(), module.getName());
		Event event = new Event("monitor.module.stop");
		event.put("system", module.getSystem().getName());
		event.put("module", module.getName());
		send(event);
	}

	//public void handleError(Exception e, String component) {
	//	errorHandler.handle(e, component);
	//	}

	public List<String> getDistributedModules() {
		return distributedModules;
	}

	public static void startConsole() {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		try {
			for (;;) {
				String line = br.readLine();
				if (line == null)
					break;
				line = line.trim();
				if (line.equalsIgnoreCase("exit")) {
					System.exit(0);
				}
			}
		} catch (IOException e) {
		}
	}

}
