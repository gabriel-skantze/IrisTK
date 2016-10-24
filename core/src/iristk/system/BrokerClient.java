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
import iristk.util.Record;
import iristk.util.Record.JsonToRecordException;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class BrokerClient {

	private Socket socket;
	private ParsedInputStream inFromServer;
	private DataOutputStream outToServer;
	private EventListener callback;
	private ClientThread clientThread;
	private String ticket;
	private String serverHost;
	private int serverPort;
	private boolean connected = false;
	private NameFilter brokerSubscribes = NameFilter.NONE;
	private String clientName;
	private boolean running;

	public BrokerClient(String ticket, String clientName, String serverHost, int serverPort, EventListener callback) {
		this.serverHost = serverHost;
		this.serverPort = serverPort;
		this.ticket = ticket.replace(" ", "_");
		this.callback = callback;
		this.clientName = clientName.replace(" ", "_");
	}

	public void connect() throws IOException {
		socket = new Socket(serverHost, serverPort);
		inFromServer = new ParsedInputStream(socket.getInputStream());
		outToServer = new DataOutputStream(socket.getOutputStream());
		sendToServer("CONNECT " + ticket + " " + clientName + "\n");
		this.clientThread = new ClientThread();
		clientThread.start();
		for (int i = 0; i < 100; i++) {
			if (connected)
				return;
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		throw new IOException("Connection to broker refused");
	}

	private class ClientThread extends Thread {

		@Override
		public void run() {
			running = true;
			try {
				while (running) {
					String line = null;
					line = inFromServer.readLine();
					if (!running)
						return;
					if (line.startsWith("EVENT")) {
						String cols[] = line.split(" ");
						String label = cols[1];
						int length = Integer.parseInt(cols[2]);
						byte[] bytes = new byte[length];
						int pos = 0;
						do {
							pos = inFromServer.read(bytes, pos, length - pos) + pos;
						} while (pos < length);
						try {
							Record record = Record.fromJSON(new String(bytes));
							if (record != null && record instanceof Event) {
								//System.out.println(((Event)record).getName());
								callback.onEvent((Event)record);
							}
						} catch (JsonToRecordException e) {
							System.out.println("Error parsing JSON: " + e.getMessage());
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else if (line.startsWith("CONNECTED")) {
						connected = true;
					} else if (line.startsWith("SUBSCRIBE")) {
						if (line.trim().length() == 9) {
							brokerSubscribes = NameFilter.NONE;	
						} else {
							String pattern = line.substring(10).trim();
							brokerSubscribes = NameFilter.compile(pattern);
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println("Stopped running");
		}
	}

	public void send(Event event) throws IOException {
		if (brokerSubscribes.accepts(event.getName())) {
			byte[] bytes = event.toJSON().toString().getBytes();
			String header = "EVENT " + event.getName() + " " + bytes.length + "\n";
			sendToServer(header, bytes);
		}
	}

	public void subscribe(NameFilter filter) throws IOException {
		if (filter == null) 
			filter = NameFilter.NONE;
		sendToServer("SUBSCRIBE " + filter.toString() + "\n");
	}

	public void close() throws IOException {
		if (running) {
			running = false;
			sendToServer("CLOSE\n");
			try {
				clientThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void sendToServer(String message) throws IOException {
		sendToServer(message, null);
	}

	private void sendToServer(String header, byte payload[]) throws IOException {
		synchronized ( outToServer ) {
			if ( header != null && header.length() > 0 ) {
				outToServer.writeBytes(header);
			}
			if ( payload != null && payload.length > 0 ) {
				outToServer.write(payload);
			}
		}
	}
}
