package iristk.kinect;

import java.awt.image.BufferedImage;

import iristk.net.kinect.ColorFrameListener;
import iristk.video.VideoEncoder;

public class KinectVideoEncoder extends VideoEncoder implements ColorFrameListener {

	private CameraImage kinectImage;
	
	public KinectVideoEncoder(IKinect kinect) throws Exception {
		kinect.addColorFrameListener(this);
	}

	@Override
	public void onColorFrameReady(long ptr, int width, int height, int format) {
		try {

			if (!isEncoding())
				return;
			
			if (kinectImage == null) {
				if (width > 1000)
					kinectImage = new CameraImage(width / 2, height / 2, BufferedImage.TYPE_3BYTE_BGR);
				else
					kinectImage = new CameraImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
			}
			kinectImage.update(ptr, width, height, format);

			encodeImage(kinectImage);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

}
