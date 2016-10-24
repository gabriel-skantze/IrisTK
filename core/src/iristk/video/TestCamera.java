package iristk.video;

import javax.swing.JFrame;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;

public class TestCamera {

	public static void main(String[] args) {
		Webcam webcam = getByName("HD USB");
		webcam.setViewSize(WebcamResolution.VGA.getSize());
		//webcam.setViewSize(new Dimension(1920, 1080));
		
		webcam.open();
		
		
		try {
			while (true) {
				Thread.sleep(1000 / 25);
				webcam.getImage();
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		WebcamPanel panel = new WebcamPanel(webcam);
		panel.setFPSDisplayed(true);
		panel.setDisplayDebugInfo(true);
		panel.setImageSizeDisplayed(true);
		panel.setMirrored(true);

		JFrame window = new JFrame("Test webcam panel");
		window.add(panel);
		window.setResizable(true);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.pack();
		window.setVisible(true);
		
	}

	public static Webcam getByName(String name) {
		for (Webcam webcam : Webcam.getWebcams()) {
			System.out.println(webcam);
			if (webcam.getName().toUpperCase().contains(name.toUpperCase()))
				return webcam;
		}
		return null;
	}
	
}
