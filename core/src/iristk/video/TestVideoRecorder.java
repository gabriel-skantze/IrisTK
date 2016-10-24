package iristk.video;
import iristk.kinect.KinectV1;
import iristk.kinect.OldKinectVideoRecorder;

import java.io.File;

public class TestVideoRecorder  {

    public TestVideoRecorder() throws Exception {
		KinectV1 kinect = new KinectV1();
    	OldKinectVideoRecorder videoRecorder = new OldKinectVideoRecorder(kinect);
    	videoRecorder.startEncoding(new File("test.mp4"));
		Thread.sleep(10000);
		kinect.stop();
		videoRecorder.finish();
    }

	public static void main(String[] args) throws Exception {
		new TestVideoRecorder();
	}
	
}
