package iristk.kinect;

import iristk.net.kinect.BeamAngleListener;

public class TestKinect implements BeamAngleListener {

	public TestKinect() throws Exception {
		KinectV2 kinect = new KinectV2();
		kinect.addBeamAngleListener(this);
		kinect.startAudioStream();
		while (true) Thread.sleep(1000);
	}
	
	public static void main(String[] args) throws Exception {
		new TestKinect();
	}

	@Override
	public void onBeamAngleChanged(float arg0) {
		System.out.println(Math.toDegrees(arg0));
	}
	
}
