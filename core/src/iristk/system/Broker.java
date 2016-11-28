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
import iristk.util.ParsedInputStream;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;

/*
> CONNECT maptask
< CONNECTED 
< SUBSCRIBE pattern
> SUBSCRIBE pattern

> EVENT action.speech 312
{"class" : "iristk.system.Event",
  "event_name" : "action.speech",
  "text" : "Hello there"}

> CLOSE

 */

public class Broker extends Thread {

	private static Logger logger = IrisUtils.getLogger(Broker.class);

	private ServerSocket socket;
	private HashMap<String,BrokerSystem> systems = new HashMap<String,BrokerSystem>();
	private List<BrokerListener> listeners = new ArrayList<>();
	private int port;

	public Broker(int port) {
		this.port = port;
	}

	@Override
	public void run() {
		try {
			logger.info("BrokerServer running on " + getIpAddress(InetAddress.getLocalHost()) + ":" + port);
			socket = new ServerSocket(port, 100);
			while (true) {
				try {
					Socket connected = socket.accept();
					//System.out.println("Connection");
					new Thread(new ServerClient(connected)).start();
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Map<String,BrokerSystem> getSystems() {
		return systems;
	}

	private synchronized void send(ServerClient sender, String label, byte[] message) {
		for (ServerClient client : systems.get(sender.ticket)) {
			if (sender != client) {
				if (client.subscribes.accepts(label)) {
					client.sendEvent(label, message);
				}
			}
		}
	}

	private synchronized void addClient(ServerClient client) {
		if (!systems.containsKey(client.ticket))
			systems.put(client.ticket, new BrokerSystem());
		for (BrokerListener listener : listeners) {
			listener.clientConnected(client);
		}
		systems.get(client.ticket).add(client);
	}

	private synchronized void removeClient(ServerClient client) {
		systems.get(client.ticket).remove(client);
		if (systems.get(client.ticket).size() == 0)
			systems.remove(client.ticket);
		for (BrokerListener listener : listeners) {
			listener.clientDisconnected(client);
		}
		updateSubscriptions(client.ticket);
		if (systems.get(client.ticket) == null)
			return;
		for (ServerClient toClient : systems.get(client.ticket)) {
			Event event = new Event("monitor.system.disconnected");
			event.put("system", client.clientName);
			toClient.sendEvent(event.getName(), event.toJSON().toString().getBytes());
		}
	}

	private synchronized void updateSubscriptions(String ticket) {
		if (systems.containsKey(ticket)) {
			for (ServerClient fromClient : systems.get(ticket)) {
				NameFilter filter = NameFilter.NONE;
				for (ServerClient toClient : systems.get(ticket)) {
					if (fromClient != toClient) {
						filter = filter.combine(toClient.subscribes);
					}
				}
				if (fromClient.latestSubscribeMessage == null || !fromClient.latestSubscribeMessage.equals(filter)) {
					try {
						fromClient.outToClient.writeBytes("SUBSCRIBE " + filter + "\n");
					} catch (IOException e) {
						e.printStackTrace();
					}
					fromClient.latestSubscribeMessage = filter;
				}
			}
		}

	}

	public static String getIpAddress(InetAddress inetAddress) {
		String result = "";
		for (byte b : inetAddress.getAddress()) {
			int bi = b & 0xFF;
			if (result.length() > 0)
				result += ".";
			result += bi;
		}
		return result;
	}

	public static interface BrokerListener {

		void clientDisconnected(ServerClient client);

		void clientConnected(ServerClient client);

	}

	public class BrokerSystem extends ArrayList<ServerClient> {

	}

	private class IllegalBrokerCommandException extends Exception {

		public IllegalBrokerCommandException(String msg) {
			super(msg);
		}

	}

	public class ServerClient implements Runnable {

		private Socket socket;
		private ParsedInputStream inFromClient = null;
		private DataOutputStream outToClient = null;
		private String ticket;
		private NameFilter subscribes = NameFilter.NONE;
		private NameFilter latestSubscribeMessage = null;
		private String clientName;

		public ServerClient(Socket socket) {
			this.socket = socket;
		}

		public synchronized void sendEvent(String name, byte[] eventBytes) {
			try {
				outToClient.writeBytes("EVENT " + name + " " + (eventBytes.length+1) + "\n");
				outToClient.write(new String(new String(eventBytes) + "\n").getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		private void disconnect() {
			synchronized (Broker.this) {
				removeClient(this);
				try {
					inFromClient.close();
					outToClient.close();
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		@Override
		public void run() {

			try {

				inFromClient = new ParsedInputStream(socket.getInputStream());
				outToClient = new DataOutputStream(socket.getOutputStream());

				HANDLE:
					while (true) {
						String line = inFromClient.readLine();
						//System.out.println(line);
						try {
							if (line != null) {
								if (line.startsWith("CONNECT ")) {
									String[] cols = line.substring(8).trim().split(" ");
									if (cols.length != 2 || cols[0].length() == 0 || cols[1].length() == 0) 
										throw new IllegalBrokerCommandException(line);
									this.ticket = cols[0];
									this.clientName = cols[1];
									logger.info("Connection to " + this.ticket + " from " + clientName + "@" + getIpAddress(socket.getInetAddress()));
									addClient(this);
									updateSubscriptions(ticket);
									outToClient.writeBytes("CONNECTED\n");
									/*
								for (ServerClient toClient : systems.get(ticket)) {
									if (toClient != this) {
										Event event = new Event("monitor.system.connected");
										event.put("system", this.clientName);
										toClient.sendEvent(event.getName(), event.toJSON().toString().getBytes());
									}
								}
									 */
								} else if (line.startsWith("EVENT ")) {
									String cols[] = line.substring(6).trim().split(" ");
									if (cols.length != 2 || cols[0].length() == 0 || cols[1].length() == 0) 
										throw new IllegalBrokerCommandException(line);
									String label = cols[0];
									int length;
									try {
										length = Integer.parseInt(cols[1]);
									} catch (NumberFormatException e) {
										throw new IllegalBrokerCommandException(line);
									}
									byte[] message = new byte[length];
									int pos = 0;
									try {
										do {
											pos = inFromClient.read(message, pos, length - pos) + pos;
										} while (pos < length);
										Broker.this.send(this, label, message);
									} catch (IOException e) {
										throw new IllegalBrokerCommandException("Could not read " + length + " bytes for event " + label);
									}
								} else if (line.startsWith("CLOSE")) {
									logger.info("Disconnection from " + this.ticket + " from " + clientName + "@" + getIpAddress(socket.getInetAddress()));
									disconnect();
									break HANDLE;
								} else if (line.startsWith("SUBSCRIBE ")) {
									String pattern = line.substring(10).trim();
									if (pattern.length() == 0)
										pattern = "**";
									try {
										this.subscribes = NameFilter.compile(pattern);
										updateSubscriptions(ticket);
									} catch (IllegalArgumentException e) {
										throw new IllegalBrokerCommandException(e.getMessage());
									}
									//subscribe = subscribe.replaceAll(".", "\\.");
									//subscribe = subscribe.replaceAll("*", ".*"); 
								}
							}
						} catch (IllegalBrokerCommandException e) {
							logger.error("Illegal Broker command: " + e.getMessage());
						}
					}
			}  catch (SocketException e) {
				logger.info("Disconnection from " + this.ticket + " from " + clientName + "@" + getIpAddress(socket.getInetAddress()));
				removeClient(this);
			}  catch (Exception e) {
				e.printStackTrace();
				removeClient(this);
			}
		}

		public Socket getSocket() {
			return socket;
		}

		public NameFilter getSubscribes() {
			return subscribes;
		}

		public String getName() {
			return clientName;
		}
	}

	public static void main(String[] args) {
		if (args.length == 1) {
			new Broker(Integer.parseInt(args[0])).start();
		} else {
			new Broker(1932).start();
		}
	}

	public void addBrokerListener(BrokerListener listener) {
		listeners.add(listener);
	}

}
