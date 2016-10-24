package iristk.video;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IPixelFormat;
import com.xuggle.xuggler.IRational;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.IVideoPicture;
import com.xuggle.xuggler.video.ConverterFactory;
import com.xuggle.xuggler.video.IConverter;

public class VideoEncoder {
	
	private IStreamCoder coder;

	private long firstTimeStamp;

	private IPacket packet;

	private int encframe;
	
	private int bitRate = 500000;

	private List<VideoPacketListener> listeners = new ArrayList<>();

	private boolean encoding = false;

	private IStreamCoder newCoder;

	public synchronized void addVideoPacketListener(VideoPacketListener listener) {
		listeners.add(listener);
	}
	
	public void setBitRate(int rate) {
		this.bitRate = rate;
	}
	
	public void startEncoding(IStreamCoder coder) {
		this.newCoder = coder;
		this.coder = null;
		encoding  = true;
	}
	
	public void stopEncoding() {
		encoding = false;
		coder = null;
	}
	
	public boolean isEncoding() {
		return encoding;
	}
	 
	public void encodeImage(BufferedImage image) {
		if (!encoding)
			return;
		
		if (coder == null) {
			coder = newCoder;
			newCoder = null;

			//ICodec codec = ICodec.findEncodingCodec(ICodec.ID.CODEC_ID_FLV1);
			//ICodec codec = ICodec.findEncodingCodec(ICodec.ID.CODEC_ID_H264);
			//coder = IStreamCoder.make(Direction.ENCODING, codec);
			coder.setCodec(ICodec.ID.CODEC_ID_H264);  
			coder.setNumPicturesInGroupOfPictures(15);
			coder.setBitRate(bitRate);
			coder.setPixelType(IPixelFormat.Type.YUV420P);
			coder.setHeight(image.getHeight());
			coder.setWidth(image.getWidth());
			//coder.setFlag(IStreamCoder.Flags.FLAG_QSCALE, true);
			//coder.setGlobalQuality(0);
			IRational frameRate = IRational.make(25, 1);
			coder.setFrameRate(frameRate);
			coder.setTimeBase(IRational.make(frameRate.getDenominator(), frameRate.getNumerator()));
			firstTimeStamp = System.currentTimeMillis();
			coder.open();
			encframe = 0;
			
		}
		
		packet = IPacket.make();
		IConverter converter = ConverterFactory.createConverter(image, IPixelFormat.Type.YUV420P);
		long timeStamp = (System.currentTimeMillis() - firstTimeStamp) * 1000; 

		//System.out.println("Encoding image " + timeStamp);
		IVideoPicture outFrame = converter.toPicture(image, timeStamp);
		if (encframe == 0) {
			//make first frame keyframe
			outFrame.setKeyFrame(true);
		}
		encframe++;
		//outFrame.setQuality(0);
		if (coder.encodeVideo(packet, outFrame, 0) < 0) {
			System.out.println("Error encoding");
		}
		// System.out.println(coder.encodeVideo(null, outFrame, -1));
		outFrame.delete();
		converter.delete();
		//System.out.println(packet.);
		
		
		if (packet.isComplete()) {
			
			for (VideoPacketListener listener : listeners) {
				listener.newVideoPacket(packet, image.getWidth(), image.getHeight());
			}
			
		}
	}

}
