package iristk.video;

import java.io.File;

import iristk.kinect.KinectV2;
import iristk.kinect.KinectVideoEncoder;

public class TestRecord {

	public static void main(String[] args) throws Exception {
		KinectV2 kinect = new KinectV2();
		KinectVideoEncoder videoEncoder = new KinectVideoEncoder(kinect);
		
		VideoRecorder recorder = new VideoRecorder();
		videoEncoder.addVideoPacketListener(recorder);
		
		videoEncoder.startEncoding(recorder.startRecording(new File("test.mp4")));
		
		Thread.sleep(5000);
		
		videoEncoder.stopEncoding();
		recorder.stopRecording();
		
		System.exit(0);
		
	}
	
}
