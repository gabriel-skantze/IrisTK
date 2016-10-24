package iristk.kinect;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.libjpegturbo.turbojpeg.TJ;
import org.libjpegturbo.turbojpeg.TJCompressor;

import iristk.net.kinect.ColorFrameListener;
import iristk.system.IrisUtils;
import iristk.system.Logger;
import iristk.video.VideoRecorder;

public class KinectLogger implements Logger, ColorFrameListener {

	private static org.slf4j.Logger slf4jLogger = IrisUtils.getLogger(KinectLogger.class);

	private VideoRecorder videoRecorder;
	private KinectVideoEncoder videoEncoder;

	private boolean enableVideo;
	private IKinect kinect;

	private File logDir;
	private long logStartTime;

	private int snapshotFreq = 0;
	private Long nextSnapshot = null;
	private boolean enableSnapshot = false;
	private CameraImage snapshotImg = null;

	private TJCompressor compressor;

	private byte[] jpegBytes = new byte[10000];

	public KinectLogger(IKinect kinect, boolean enableVideo) {
		this.kinect = kinect;
		kinect.addColorFrameListener(this);
		this.enableVideo = enableVideo;
		try {
			compressor = new TJCompressor();
			compressor.setJPEGQuality(50);
			compressor.setSubsamp(TJ.SAMP_420);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public synchronized void enableVideo(boolean b) {
		this.enableVideo = b;
	}

	public synchronized void enableSnapshot(boolean b, int freq) {
		this.snapshotFreq = freq;
		this.enableSnapshot  = b;
	}

	@Override
	public synchronized void startLogging(File logDir) throws IOException {
		try {
			this.logStartTime = System.currentTimeMillis();
			this.logDir = logDir;
			if (enableSnapshot) {
				nextSnapshot = System.currentTimeMillis();
				new File(logDir, "snapshots").mkdirs();
			} else {
				nextSnapshot = null;
			}
			if (enableVideo) {
				if (videoEncoder == null) {
					videoEncoder = new KinectVideoEncoder(kinect);
					videoRecorder = new VideoRecorder();
					videoEncoder.addVideoPacketListener(videoRecorder);
				}
				videoEncoder.startEncoding(videoRecorder.startRecording(new File(logDir, "kinect.mp4")));
			}
		} catch (Exception e) {
			slf4jLogger.error("Problem logging Kinect video", e);
		}
	}

	@Override
	public synchronized void stopLogging() throws IOException {
		if (videoRecorder != null && videoRecorder.isRecording()) {
			try {
				videoEncoder.stopEncoding();
				videoRecorder.stopRecording();
			} catch (IOException e) {
				slf4jLogger.error("Problem stopping video recording", e);
			}
		}
		nextSnapshot = null;
	}


	@Override
	public synchronized void onColorFrameReady(long ptr, int width, int height, int format) {
		if (nextSnapshot != null && System.currentTimeMillis() >= nextSnapshot) {
			try {
				if (snapshotImg == null)
					snapshotImg = new CameraImage(width/2, height/2);
				snapshotImg.update(ptr, width, height, format);
				File snapshotImgFile = new File(logDir, "snapshots/" + String.format("%06d", (System.currentTimeMillis() - logStartTime)) + ".jpg");
				imageToJpeg(snapshotImg, snapshotImgFile);
				File folderImgFile = new File(logDir, "folder.jpg");
				if (!folderImgFile.exists()) {
					CameraImage folderImg = new CameraImage(width/4, height/4);
					folderImg.update(ptr, width, height, format);
					imageToJpeg(folderImg, folderImgFile);
				}
			} catch (Exception e) {
				slf4jLogger.error("Problem taking snapshot", e);
			}
			if (snapshotFreq == 0)
				nextSnapshot = null;
			else
				nextSnapshot += snapshotFreq;
		} 
	}

	private void imageToJpeg(BufferedImage image, File file) {
		boolean done = false;
		while (!done)
			try {
				compressor.compress(image, jpegBytes, 0);
				done = true;
			} catch (Exception e) {
				if (e.getMessage().startsWith("Destination buffer is not large enough")) {
					jpegBytes = new byte[jpegBytes.length * 2];
					//System.out.println("Increasing buffer to " + jpegBytes.length);
				} else {
					return;
				}
			}
		try {
			FileOutputStream out = new FileOutputStream(file);
			int size  = compressor.getCompressedSize();
			out.write(jpegBytes, 0, size);
			out.close();
		} catch (IOException e) { 
			e.printStackTrace();
		}
	}



}
