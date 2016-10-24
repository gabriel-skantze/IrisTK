package iristk.video;

import iristk.system.IrisUtils;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;

public class VideoRecorder implements VideoPacketListener {
	
	private static Logger logger = IrisUtils.getLogger(VideoRecorder.class);

	private IContainer container;
	private boolean recording = false;

	private boolean headerWritten = false;

	public IStreamCoder startRecording(File file) throws IOException {
		container = IContainer.make();
		if (container.open(file.getAbsolutePath(), IContainer.Type.WRITE, null) < 0)
			throw new IOException("Failed to start recording");
		recording = true;
		headerWritten = false;
		IStream stream = container.addNewStream(0);  
		return stream.getStreamCoder();  
	}

	public void stopRecording() throws IOException {
		recording = false;
		if (container.writeTrailer() < 0)
			throw new IOException("Failed to stop recording");
		container.close();
	}
	
	@Override
	public void newVideoPacket(IPacket packet, int width, int height) {
		if (recording) {
			try {
				/*
				if (coder == null) {
					IStream stream = container.addNewStream(0);  
					coder = stream.getStreamCoder();  
					//coder.setCodec(ICodec.ID.CODEC_ID_FLV1);  
					coder.setCodec(ICodec.ID.CODEC_ID_H264);  
					coder.setPixelType(IPixelFormat.Type.YUV420P);
					IRational frameRate = IRational.make(25, 1);
					coder.setFrameRate(frameRate);
					coder.setTimeBase(IRational.make(frameRate.getDenominator(), frameRate.getNumerator()));
					coder.setHeight(height);
					coder.setWidth(width);
					if (coder.open()<0) throw new IOException("could not open coder"); 
		
					if (container.writeHeader() < 0)
						throw new IOException("could not write header");
				}
				*/
				if (!headerWritten) {
					if (container.writeHeader() < 0)
						logger.error("Could not write header");
					headerWritten = true;
				}
				packet.setStreamIndex(0);
				if (container.writePacket(packet) < 0)
					throw new IOException("could not write packet");
			} catch (Exception e) {
				recording = false;
				logger.error("Problem writing video packet", e);
			}
		}
	}

	public boolean isRecording() {
		return recording;
	}

}
