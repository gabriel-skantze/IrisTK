package iristk.kinect;

import iristk.net.kinect.*;

public interface IKinect {

	String getId();

	void setMinDistance(float f);
	void setMaxDistance(float f);
	
	float getVerticalFOV();
	float getHorizontalFOV();

	void addBodyListener(BodyListener listener);
	void addDepthFrameListener(DepthFrameListener listener);
	void addColorFrameListener(ColorFrameListener listener);
	void addSensorElevationListener(SensorElevationListener listener);
	void addBeamAngleListener(BeamAngleListener listener);

	void startAudioStream();
	void stopAudioStream();
	byte[] readAudioStream(int len);
	
	void stop();

	int[] mapSkeletonPointToColorPoint(float x, float y, float z);
	float[] mapColorPointToCameraSpace(int x, int y);

	void enableDepthMapping();

	int getCameraViewHeight();
	int getCameraViewWidth();

}
