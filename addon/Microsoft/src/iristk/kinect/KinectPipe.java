package iristk.kinect;

import iristk.net.kinect.ColorFrameListener;

public class KinectPipe implements ColorFrameListener {

	private CameraImage image;

	public KinectPipe() throws InterruptedException {
		KinectV2 kinect = new KinectV2();
		kinect.addColorFrameListener(this);
		while (true) Thread.sleep(1000);
	}
	
	public static void main(String[] args) throws Exception {
		new KinectPipe();
	}

	@Override
	public void onColorFrameReady(long ptr, int width, int height, int format) {
			try {
				if (image == null) {
					image = new CameraImage(width, height);
				}
				image.update(ptr, width, height, format);
				//System.out.println(ptr);
				if (image.bytePixels != null)
					System.out.write(image.bytePixels);
				//else if (image.intPixels != null)
				//	System.out.write(image.intPixels);
			} catch (Exception e) {
				e.printStackTrace();
			}
		
	}
	
}
