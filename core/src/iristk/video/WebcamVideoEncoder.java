package iristk.video;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;

public class WebcamVideoEncoder extends VideoEncoder {

	private Webcam webcam;
	private CaptureThread captureThread;

	private boolean encoding = false;

	public WebcamVideoEncoder() {
		webcam = Webcam.getDefault();
		webcam.setViewSize(WebcamResolution.VGA.getSize());
	}

	private class CaptureThread extends Thread {

		@Override
		public void run() {
			try {
				while (encoding) {
					long t = System.currentTimeMillis();
					BufferedImage image = webcam.getImage();
					BufferedImage convertedImg = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
					//System.out.println("A: " + ( System.currentTimeMillis() - t));
					t = System.currentTimeMillis();
					Graphics g = convertedImg.getGraphics();
					g.drawImage(image, 0, 0, null);
					g.dispose();
					//System.out.println("B: " + ( System.currentTimeMillis() - t));
					t = System.currentTimeMillis();
					encodeImage(convertedImg);
					//System.out.println("C: " + ( System.currentTimeMillis() - t));
					Thread.sleep(1000 / 25);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void startEncoding() {
		webcam.open();
		encoding = true;
		new CaptureThread().start();
	}

	@Override
	public void stopEncoding() {
		encoding = false;
	}

}
