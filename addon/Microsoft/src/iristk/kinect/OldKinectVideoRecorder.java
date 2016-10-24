package iristk.kinect;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainerFormat;
import com.xuggle.xuggler.io.XugglerIO;

import iristk.net.kinect.ColorFrameListener;

public class OldKinectVideoRecorder implements ColorFrameListener {

    private CameraImage kinectImage;
	//private float tot;
	//private int c = 0;
    
	private IMediaWriter writer;
	private long startTime;

	private boolean encoding = false;
	
	public OldKinectVideoRecorder(IKinect kinect) {
		kinect.addColorFrameListener(this);
	}
    
	@Override
	public void onColorFrameReady(long ptr, int width, int height, int format) {
		try {
			if (!encoding)
				return;
			if (kinectImage == null) {
				kinectImage = new CameraImage(width / 2, height / 2, BufferedImage.TYPE_3BYTE_BGR);
				writer.addVideoStream(0,  0, ICodec.ID.CODEC_ID_H264, width / 2, height / 2);
				//writer.addVideoStream(0,  0, ICodec.ID.CODEC_ID_MJPEG, width / 2, height / 2);
			}
			//long t = System.currentTimeMillis();
			kinectImage.update(ptr, width, height, format);
			//tot += (System.currentTimeMillis() - t);
			//c++;
			System.out.println("Encoding image");
			writer.encodeVideo(0, kinectImage, System.nanoTime()-startTime, TimeUnit.NANOSECONDS);
			//System.out.println("B: " + (System.currentTimeMillis() - t));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    
	public void startEncoding(File out) throws Exception {
		writer = ToolFactory.makeWriter(out.getAbsolutePath());
		encoding = true;
	}
	
	public void startEncoding(OutputStream out) {
		// create a media writer and specify the output stream
		writer = ToolFactory.makeWriter(XugglerIO.map(out));
				
		// manually set the container format (because it can't detect it by filename anymore)
		IContainerFormat containerFormat = IContainerFormat.make();
		//containerFormat.setOutputFormat("ogg", null, "application/ogg");
		containerFormat.setOutputFormat("mp4", null, "application/mp4");
		//containerFormat.setOutputFormat("mjpeg", null, "application/x-motion-jpeg");
		writer.getContainer().setFormat(containerFormat);
		System.out.println("Start encoding");
		startTime = System.nanoTime();
		encoding = true;
		// add the video stream
		//writer.addVideoStream(videoStreamIndex, videoStreamId, ICodec.ID.CODEC_ID_THEORA, width, height);
	}
	
    public void finish() throws IOException {
    	encoding = false;
        writer.close();
    }
	
}
