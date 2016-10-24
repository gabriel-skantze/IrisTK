/*******************************************************************************
 * Copyright (c) 2014 Gabriel Skantze.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Gabriel Skantze - initial API and implementation
 ******************************************************************************/
package iristk.kinect;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;

import javax.imageio.ImageIO;

import org.slf4j.Logger;

import iristk.net.kinect.*;
import iristk.situated.Body;
import iristk.situated.BodyPart;
import iristk.situated.Location;
import iristk.situated.Rotation;
import iristk.situated.SensorModule;
import iristk.system.Event;
import iristk.system.IrisUtils;
import iristk.util.Profiler;
import iristk.util.Record;
import iristk.vision.FaceRecognizer;
import iristk.vision.FaceRecognizerListener;

public class KinectModule extends SensorModule implements KinectBodyListener, SensorElevationListener {

	private static Logger logger = IrisUtils.getLogger(KinectModule.class);
	
	public static int BODY_EXPIRE = 2000;

	// Parameter to put a ceiling on number of events per second
	private int millisecondsInterval = 250;
	
	private int nextUnusedBodyId = 1;

	private HashMap<String, BodySeen> bodyMapping = new HashMap<String, BodySeen>();

	private IKinect kinect;
	
	private HeadRotationBuffer bodyBuffer = new HeadRotationBuffer();

	private KinectFaceRecognizer faceRecognizer;

	private BodyListenerThread bodyListenerThread;

	public KinectModule(IKinect kinect) {
		initialize(kinect);
	}

	public KinectModule() {
		this("kinect");
	}
	
	public KinectModule(String id) {
		try {
			initialize(new KinectV1(id));
		} catch (Exception e) {
			initialize(new KinectV2(id));
		}
	}
	
	private void initialize(IKinect kinect) {
		this.kinect = kinect;
		bodyListenerThread = new BodyListenerThread();
		kinect.addBodyListener(bodyListenerThread);
		addBodyListener(this);
		kinect.setMinDistance(0.8f);
		kinect.setMaxDistance(2.0f);
		kinect.addSensorElevationListener(this);
		setSensor(new KinectSensor(kinect), true);
	}
	
	
	Profiler profiler = new Profiler(100);

	private Boolean active = true;
	
	private class BodyListenerThread extends Thread implements BodyListener {
		
		public ArrayBlockingQueue<Record> bodies = new ArrayBlockingQueue<>(1000);
		public ArrayList<KinectBodyListener> listeners = new ArrayList<>();
		
		public BodyListenerThread() {
			start();
		}
		
		@Override
		public void run() {
			try {
				while (true) {
					Record bodyset = bodies.take();
					synchronized (bodyListenerThread) {
						//profiler.start();
						for (KinectBodyListener listener : listeners) {
							try {
								listener.onBodiesReceived(bodyset);
								//profiler.point(listener.getClass().getSimpleName());
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						//profiler.done();
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onBodiesReceived(String bodyDataString) {
			try {
				//profiler.start();
				//System.out.println(bodyDataString);
				bodies.add(Record.fromJSON(bodyDataString));
				//profiler.done();
			} catch (Exception e) {
				logger.error("Cannot parse JSON: " + bodyDataString);
			}
		}
		
	}
	
	public void addBodyListener(KinectBodyListener listener) {
		synchronized (bodyListenerThread) {
			bodyListenerThread.listeners.add(listener);
		}
	}
	
	public void setFaceRecognizer(FaceRecognizer recognizer) {
		this.faceRecognizer = new KinectFaceRecognizer(recognizer);
		kinect.addColorFrameListener(faceRecognizer);
	}
	
	private class KinectFaceRecognizer implements FaceRecognizerListener, ColorFrameListener {

		private CameraImage colorImage;
		private FaceRecognizer recognizer;
		private Set<String> names;
		private Map<String,BodySeen> bodiesSeen = new HashMap<>();
		
		public KinectFaceRecognizer(FaceRecognizer recognizer) {
			this.recognizer = recognizer;
			names = new HashSet<>(recognizer.getNames());
			recognizer.addFaceRecognizerListener(this);
		}

		@Override
		public void onColorFrameReady(long ptr, int width, int height, int format) {
			try {
				if (colorImage == null) {
					colorImage = new CameraImage(width, height);
				}
				colorImage.update(ptr, width, height, format);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void faceRecognized(String id, String name, double conf) {
			System.out.println("Recognition succeeded: " + name);
			BodySeen bodySeen = bodiesSeen.get(id);
			bodySeen.recognizing = false;
			bodySeen.recognizedName = name;
		}

		@Override
		public void faceUnknown(String id) {
			System.out.println("Recognition failed");
			BodySeen bodySeen = bodiesSeen.get(id);
			bodySeen.recognizing = false;
		}

		public void recognize(Body body, BodySeen bodySeen) {
			try {
				if (colorImage == null || body.head.location.z > 2 || body.head.rotation == null || Math.abs(body.head.rotation.x) > 20 || Math.abs(body.head.rotation.y) > 20 || Math.abs(body.head.rotation.z) > 20) {
					return;
				}
			    bodiesSeen.put(body.id, bodySeen);
				bodySeen.recognizing = true;
				bodySeen.recognizedAttempts++;
				System.out.println("Trying to recognize " + body.id + " for the " + bodySeen.recognizedAttempts + " time");
				int[] xy = kinect.mapSkeletonPointToColorPoint((float)body.head.location.x, (float)body.head.location.y, (float)body.head.location.z);
				int[] xy2 = kinect.mapSkeletonPointToColorPoint((float)body.head.location.x, (float)body.head.location.y-0.2f, (float)body.head.location.z);
				int w = Math.abs(xy[1] - xy2[1]);
				BufferedImage faceImage = colorImage.getSubimage(xy[0]-w, xy[1]-w, w*2, w*2);
			    File imageFile = IrisUtils.getTempDir("FaceRecognition/" + body.id + "_" + System.currentTimeMillis() + ".jpg");
			    imageFile.getParentFile().mkdirs();
			    ImageIO.write(faceImage, "jpg", imageFile);
			    bodySeen.faces.add(imageFile);
			    recognizer.recognize(body.id, imageFile);
			} catch (IOException e) {
			    e.printStackTrace();
			}
		}

		public void train(BodySeen bodySeen) {
			for (int pn = 0; pn < 1000; pn++) {
				String name = "person-" + pn;
				if (!names.contains(name)) {
					names.add(name);
					for (File face : bodySeen.faces) {
						recognizer.addFace(name, face);
					}
					recognizer.train();
					bodySeen.recognizedName = name;
					return;
				}
			}
		}
		
	}

	public IKinect getKinect() {
		return kinect;
	}
	
	@Override
	public void onBodiesReceived(Record bodyData) {
		if (!isRunning())
			return;

		try {
			
			Event event = new Event("sense.body");
			int nbodies = 0;
			
			List bodies = bodyData.getList("bodies");
			
			for (int i = 0; i < bodies.size(); i++) {

				//profiler.start();
				Body kbody = (Body) bodies.get(i);
				//profiler.point("A0");
				String idString = kbody.id;
				
				Body body = new Body();
				//profiler.point("A1");
				body.head = new BodyPart((Location) kbody.head.location.clone());
						//new BodyPart(kbody.getHead().getLocX(), kbody.getHead().getLocY(), kbody.getHead().getLocZ());
				
				//profiler.point("A");
				//bodyBuffer.add(kbody);
				body.head.rotation = bodyBuffer.add(kbody);
				//profiler.point("B");
				
				if (body.head.rotation != null && (body.head.rotation.x != 0 || body.head.rotation.y != 0 || body.head.rotation.z != 0)) {
					// Rotation as calculated by the angle from the body to the kinect
					Rotation rot = body.head.location.toRotation();
					rot.x = -rot.x;
					// Here we add the kinect face rotation
					body.head.rotation = rot.add(body.head.rotation);
				} else {
					body.head.rotation = null;
				}
				if (kbody.handLeft != null)
					body.handLeft = kbody.handLeft;
				if (kbody.handRight != null) {
					body.handRight = kbody.handRight;
				}
				
				BodySeen bodySeen;
				
				//profiler.point("C");
				
				if (bodyMapping.containsKey(idString)) {
					bodySeen = bodyMapping.get(idString);
				} else {
					bodySeen = new BodySeen(kinect.getId() + "-body-" + nextUnusedBodyId++);
					bodyMapping.put(idString, bodySeen);
				}
		
				bodySeen.lastSeen = System.currentTimeMillis();
				
				//profiler.point("D");
				
				if (bodySeen.lastReported + millisecondsInterval > bodySeen.lastSeen) return;
	
				body.expire = BODY_EXPIRE;
				body.id = bodySeen.id;
		
				if (faceRecognizer != null) {
					if (!bodySeen.recognizing ) {
						if (bodySeen.recognizedName == null && bodySeen.recognizedAttempts < 5) {
							faceRecognizer.recognize(body, bodySeen);
						} else {
							if (bodySeen.recognizedName == null) {
								faceRecognizer.train(bodySeen);
							}
							body.recId = bodySeen.recognizedName;
						}
					}
				}
				
				body = processBody(body);
				
				bodySeen.lastReported = bodySeen.lastSeen;
							
				event.put("bodies:" + bodySeen.id, body);
				
				nbodies++;
			
				//profiler.done();
				
			}
	
			if (active && nbodies > 0) {
				event.put("sensor", kinect.getId());
				send(event);
			}
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String getBodyId(String kinectBodyId) {
		BodySeen body = bodyMapping.get(kinectBodyId);
		if (body != null)
			return body.id;
		else
			return null;
	}
	
	protected Body processBody(Body body) {
		return body;
	}
	
	private class BodySeen {

		public BodySeen(String id) {
			lastReported = lastSeen = System.currentTimeMillis();
			this.id = id;
		}
		
		public String id;
		
		public long lastSeen;
		public long lastReported;

		public String recognizedName = null;
		public int recognizedAttempts = 0;
		public boolean recognizing = false;

		public List<File> faces = new ArrayList<>();
	}
	
	@Override
	public void onSensorElevationChanged(int angle) {
		if (getSensor().rotation != null) {
			getSensor().rotation.x  = -angle;
			sendSensorDetect();
		}
	}
	
	@Override
	public void onSensorElevationReadingFailed() {
		logger.warn("Sensor elevation reading failed");
	}

	public void setActive(Boolean active) {
		this.active  = active;
	}
	
}
