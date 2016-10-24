package iristk.audio;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class NetworkAudioStreamer implements AudioListener {

	private DatagramSocket sendSocket;
	private InetAddress address;
	private int remotePort;
	private boolean streaming;
	//private long receiveTime;
	//private ReceiveThread receiveThread;
	
	public NetworkAudioStreamer(AudioPort audioPort) throws SocketException {
		audioPort.addAudioListener(this);
	}
	
	public synchronized void startStreaming(String remoteAddress, int localPort, int remotePort) throws SocketException, UnknownHostException {
		this.sendSocket = new DatagramSocket(localPort);
		this.remotePort = remotePort;
		this.address = InetAddress.getByName(remoteAddress);
		//receiveTime = System.currentTimeMillis();
		streaming = true;
		//receiveThread = new ReceiveThread();
	}
	
	public synchronized void stopStreaming() {
		System.out.println("Stopped streaming audio");
		streaming  = false;
		sendSocket.close();
		/*
		try {
			receiveThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		*/
	}
	
	/*
	private class ReceiveThread extends Thread {

		public ReceiveThread() {
			start();
		}

		@Override
		public void run() {
			byte[] data = new byte[16];
			try {
				while (streaming) {
					DatagramPacket datagram = new DatagramPacket(data, data.length);
					sendSocket.receive(datagram);
					receiveTime = System.currentTimeMillis();
				}
			} catch (IOException e) {
				if (!sendSocket.isClosed())
					e.printStackTrace();
			}
		}
	}
	*/

	@Override
	public void listenAudio(byte[] buffer, int pos, int len) {
		if (streaming && address != null) {
			try {
				sendSocket.send(new DatagramPacket(buffer, len, address, remotePort));
				//if (System.currentTimeMillis() - receiveTime > 5000)
				//	stopStreaming();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void startListening() {
	}

	@Override
	public void stopListening() {
	}
	
	
}
