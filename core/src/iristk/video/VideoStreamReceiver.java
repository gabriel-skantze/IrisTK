package iristk.video;
import iristk.util.BlockingByteQueue;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;

import com.xuggle.ferry.IBuffer;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IPixelFormat;
import com.xuggle.xuggler.IRational;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.IVideoPicture;
import com.xuggle.xuggler.IStreamCoder.Direction;
import com.xuggle.xuggler.video.ConverterFactory;
import com.xuggle.xuggler.video.IConverter;

public class VideoStreamReceiver {

	private List<VideoPacketListener> videoPacketListeners = new ArrayList<>();
	private List<VideoImageListener> videoImageListeners = new ArrayList<>();

	public void startReceiving(int port) {
		new DecodeThread(port);
	}
	
	public synchronized void addVideoPacketListener(VideoPacketListener listener) {
		videoPacketListeners.add(listener);
	}
	
	public synchronized void addImageListener(VideoImageListener listener) {
		videoImageListeners.add(listener);
	}
	
	private class DecodeThread extends Thread {

		IStreamCoder decoder;
		IVideoPicture picture = null;
		private IConverter converter;
		private DatagramSocket receiveSocket;
		private int partCount = 0;
		private int currentFrame = -1;
		private BlockingByteQueue queue = new BlockingByteQueue();
		
		public DecodeThread(int port) {
			try {
				receiveSocket = new DatagramSocket(port);
			} catch (SocketException e) {
				e.printStackTrace();
			}
			ICodec codec = ICodec.findDecodingCodec(ICodec.ID.CODEC_ID_FLV1);
			decoder = IStreamCoder.make(Direction.DECODING, codec);
			decoder.setPixelType(IPixelFormat.Type.YUV420P);
			IRational frameRate = IRational.make(15, 1);
			decoder.setFrameRate(frameRate);
			decoder.setTimeBase(IRational.make(frameRate.getDenominator(), frameRate.getNumerator()));
			decoder.open();
			start();
		}
		
		@Override
		public void run() {
			byte[] data = new byte[100000];
			ShortBuffer shorts = ByteBuffer.wrap(data).asShortBuffer();
			while (true) {
				DatagramPacket dpacket = new DatagramPacket(data, data.length);
				try {
					receiveSocket.receive(dpacket);
				} catch (IOException e) {
					e.printStackTrace();
				}
				int frameN = shorts.get(0);
				int partN = shorts.get(1);
				int partTot = shorts.get(2);
				int fwidth = shorts.get(3);
				int fheight = shorts.get(4);
				int length = dpacket.getLength() - VideoStreamer.HEADER_SIZE;
				
				//System.out.println(frameN + " " + partN + " " + partTot);
				
				if (frameN != currentFrame) {
					partCount = 0;
					currentFrame = frameN;
					queue.reset();
					//System.out.println("Reset: " + frameN);
				}				
							
				if (partN == partCount) {
					//System.out.println("Writing part " + partN + "/" + partCount);
					
					queue.write(data, VideoStreamer.HEADER_SIZE, length);
					partCount++;
					
					if (partCount == partTot) {
						//System.out.println("Done: " + queue.available());
						
						IBuffer buffer = IBuffer.make(null, queue.getBuffer(), 0, queue.available());
						IPacket packet = IPacket.make(buffer); 
						
						for (VideoPacketListener listener : videoPacketListeners) {
							listener.newVideoPacket(packet, fwidth, fheight);
						}

						if (videoImageListeners.size() > 0) {
						
							if (picture == null || picture.getWidth() != fwidth || picture.getHeight() != fheight) {
								//System.out.println("new picture");
								picture = IVideoPicture.make(IPixelFormat.Type.YUV420P, fwidth, fheight);
								converter = ConverterFactory.createConverter(new BufferedImage(fwidth, fheight, BufferedImage.TYPE_3BYTE_BGR), 
										IPixelFormat.Type.YUV420P);
							}
							
							int len = decoder.decodeVideo(picture, packet, 0);
							if (len < 0) {
								System.out.println("Decode error");
							} else {
								if (len < packet.getSize()) {
									System.out.println("Could not read whole package");
								}
								//System.out.println("Decoded " + len + " of " + packet.getSize() + " complete:" + picture.isComplete());
								if (picture.isComplete()) {
									BufferedImage image = converter.toImage(picture);
									for (VideoImageListener listener : videoImageListeners) {
										listener.newVideoImage(image);
									}
									/*
									byte[] msg = new byte[16];
									try {
										receiveSocket.send(new DatagramPacket(msg, 0, 16, dpacket.getAddress(), dpacket.getPort()));
									} catch (IOException e) {
										e.printStackTrace();
									}
									*/
								}
							}
						}
						
						buffer.delete();
					}
				} else {
					currentFrame = -1;
					partCount = 0;
				}
				
			
			}
		}
	}
	
}
