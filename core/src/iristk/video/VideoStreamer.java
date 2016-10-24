package iristk.video;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import com.xuggle.xuggler.IPacket;

public class VideoStreamer implements VideoPacketListener {

	private DatagramSocket sendSocket;
	private InetAddress address;
	private int remotePort;
	private boolean streaming = false;
	private int frameN = 0;

	//private long receiveTime;
	//private ReceiveThread receiveThread;

	public static int HEADER_SIZE = 10;
	private static int MAX_PACKET_SIZE = 1472;

	public synchronized void startStreaming(String remoteAddress, int localPort, int remotePort) {
		try {
			if (streaming)
				stopStreaming();
			this.address = InetAddress.getByName(remoteAddress);
			this.remotePort = remotePort;
			sendSocket = new DatagramSocket(localPort);
			//receiveTime = System.currentTimeMillis();
			streaming = true;
			//receiveThread = new ReceiveThread();
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
	}
	
	public InetAddress getAddress() {
		return address;
	}

	public synchronized void stopStreaming() {
		System.out.println("Stopped streaming video");
		streaming  = false;
		sendSocket.close();
		//try {
		//	receiveThread.join();
		//} catch (InterruptedException e) {
		//	e.printStackTrace();
		//}
	}
	
	public boolean isStreaming() {
		return streaming;
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
	public void newVideoPacket(IPacket packet, int width, int height) {
		if (streaming) {
			try {
				int packetSize = packet.getSize(); 
				//System.out.println(packetSize);
				int rest = packetSize;
				int partN = 0;
				int pos = 0;
				int partTot = ((packetSize - 1) / (MAX_PACKET_SIZE - HEADER_SIZE)) + 1;
				while (rest > 0) {
					int partSize = Math.min(rest + HEADER_SIZE, MAX_PACKET_SIZE);
					byte[] data = new byte[partSize];
					packet.getData().get(pos, data, HEADER_SIZE, partSize - HEADER_SIZE); 
					short[] shorts = new short[HEADER_SIZE / 2];
					shorts[0] = (short) frameN;
					shorts[1] = (short) partN;
					shorts[2] = (short) partTot;
					shorts[3] = (short) width;
					shorts[4] = (short) height;
					ByteBuffer.wrap(data).asShortBuffer().put(shorts);
					sendSocket.send(new DatagramPacket(data, data.length, address, remotePort));
					//System.out.println(frameN + ": Send part " + partN + "/" + partTot + " size: " + (partSize - HEADER_SIZE));
					rest = rest - (partSize - HEADER_SIZE);
					pos += (partSize - HEADER_SIZE);
					partN++;
				}
				frameN++;
				//if (System.currentTimeMillis() - receiveTime > 5000)
				//	stopStreaming();
			} catch (UnknownHostException e) {
				e.printStackTrace();
				streaming = false;
			} catch (IOException e) {
				e.printStackTrace();
				streaming = false;
			}
		}
	}

	public void addListener() {
		// TODO Auto-generated method stub
		
	}

}
