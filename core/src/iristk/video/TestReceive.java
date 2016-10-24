package iristk.video;

import java.awt.Dimension;

import javax.swing.JFrame;

public class TestReceive {

	public static void main(String[] args) throws Exception {
		//AudioStreamSource source = new AudioStreamSource(TestSend.AUDIO_RECEIVE_PORT);
		//source.addAudioListener(new Speaker());
		
		VideoStreamReceiver receiver = new VideoStreamReceiver();
		JFrame window = new JFrame();
		VideoPanel panel = new VideoPanel();
		receiver.addImageListener(panel);
		window.add(panel);
		window.setSize(new Dimension(640, 480));
		window.setVisible(true);
		receiver.startReceiving(TestSend.VIDEO_RECEIVE_PORT);
	}
	
}
