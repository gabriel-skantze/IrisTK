package iristk.video;

import iristk.kinect.IKinect;
import iristk.kinect.KinectV2;
import iristk.kinect.KinectVideoEncoder;

public class TestSend {
	
	public static int AUDIO_SEND_PORT = 8085;
	public static int AUDIO_RECEIVE_PORT = 8086;
	public static int VIDEO_SEND_PORT = 8087;
	public static int VIDEO_RECEIVE_PORT = 8088;

	public static void main(String[] args) throws Exception {
		//String address = "130.237.67.185";
		String address = "127.0.0.1";
		
		IKinect kinect = new KinectV2();
		KinectVideoEncoder videoEncoder = new KinectVideoEncoder(kinect);
		//WebcamVideoEncoder videoEncoder = new WebcamVideoEncoder();
		
		VideoStreamer videoStreamer = new VideoStreamer();
		//videoEncoder.setBitRate(800000);
		videoEncoder.addVideoPacketListener(videoStreamer);
		
		videoStreamer.startStreaming(address, VIDEO_SEND_PORT, VIDEO_RECEIVE_PORT);
		//videoEncoder.startEncoding();
		
		//AudioStreamer audioStreamer = new AudioStreamer(new Microphone());
		//audioStreamer.startStreaming(address, AUDIO_SEND_PORT, AUDIO_RECEIVE_PORT);
		
		while(true) {
			Thread.sleep(1000);
		}
		
	}
	
}
