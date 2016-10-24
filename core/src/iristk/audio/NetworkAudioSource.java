package iristk.audio;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.ArrayBlockingQueue;

import javax.sound.sampled.AudioFormat;

import org.mozilla.javascript.edu.emory.mathcs.backport.java.util.Arrays;

public class NetworkAudioSource extends AudioSource {

	private DatagramSocket socket;	

	//private BlockingByteQueue queue = new BlockingByteQueue();
	private AudioFormat audioFormat = AudioUtil.getAudioFormat(16000, 1);

	//byte[] buf = new byte[10000];

	ArrayBlockingQueue<byte[]> queue = new ArrayBlockingQueue<byte[]>(100);

	private int bufferSize = 12; // 120 ms

	public NetworkAudioSource(int port) throws SocketException {
		socket = new DatagramSocket(port);
		new ReadThread().start();
		start();
	}
	
	public void setBufferSize(int msec) {
		this.bufferSize = msec / 10;
	}

	private class ReadThread extends Thread {
		@Override
		public void run() {
			try {
				//int count = 0;
				while (true) {
					byte[] buf = new byte[320];
					DatagramPacket packet = new DatagramPacket(buf, buf.length);

					socket.receive(packet);
					//while (queue.size() > bufferSize)
					//	queue.remove();
					queue.add(buf);

					//System.out.println("A: " + packet.getLength());
					//queue.write(buf, 0, packet.getLength());

					//count++;
					//if (count % 50 == 0) {
						// Send a response every now and then to tell that we are alive
					//	byte[] msg = new byte[16];
					//	socket.send(new DatagramPacket(msg, 0, msg.length, packet.getAddress(), packet.getPort()));
					//}

				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected int readSource(byte[] buffer, int pos, int len) {

		/*
		if (queue.available() < minBufferSize * 320) {
			byte[] silence = new byte[320 * 2];
			Arrays.fill(silence, (byte)0);
			queue.write(silence, 0, silence.length);
			System.out.println("Silence " + queue.available());
		} 

		try {
			while (queue.available() > maxBufferSize * 320) {
				queue.skip(320);
				System.out.println("Skipping " + queue.available());
			}
			queue.read(buffer, pos, len);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		 */

		if (queue.size() < 1) {
			// If the queue runs dry, fill it halfway with silence
			for (int i = 0; i < bufferSize; i++) {
				byte[] silence = new byte[320];
				Arrays.fill(silence, (byte)0);
				queue.add(silence);
			}
			//System.out.println("Adding frames");
		}

		try {
			// Make sure the queue doesn't grow too large
			while (queue.size() > bufferSize) {
				queue.take();
				//System.out.println("Removing frames");
			}
			//System.out.println(queue.size());
			byte[] buf = queue.take();
			System.arraycopy(buf, 0, buffer, 0, 320);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}


		return len;
	}

	@Override
	protected void startSource() {
		//queue.reset();
	}

	@Override
	protected void stopSource() {
	}

	@Override
	public AudioFormat getAudioFormat() {
		return audioFormat ;
	}

}
