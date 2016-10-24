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

import iristk.net.kinect.BeamAngleListener;
import iristk.speech.Recognizer;
import iristk.speech.RecognizerModule;
import iristk.system.Event;
import iristk.system.InitializationException;

public class KinectRecognizerModule extends RecognizerModule implements BeamAngleListener {

	private static final long ONSET_DELAY = 500;
	
	private float currentAngle = 0;
	private KinectModule kinect;
	private DelayedDispatchThread dispatchThread;
	
	public KinectRecognizerModule(KinectModule kinect) throws InitializationException {
		super(new KinectRecognizer(new KinectAudioSource(kinect.getKinect())));
		setup(kinect);
	}
	
	public KinectRecognizerModule(KinectModule kinect, Recognizer recognizer) {
		super(recognizer);
		setup(kinect);
	}
	
	private void setup(KinectModule kinect) {
		setSensor(kinect.getSensor(), false);
		kinect.getKinect().addBeamAngleListener(this);
		this.kinect = kinect;
	}
	
	@Override
	public void send(Event event) {
		if (event.getName().equals("sense.speech.start")) {
			// We delay the speech onset to make sure that we get the beam angle change 
			dispatchThread = new DelayedDispatchThread(event);
		} else if (event.getName().startsWith("sense.speech")) {
			if (dispatchThread != null) {
				dispatchThread.terminate();
			}
			dispatch(event);
		} else {
			super.send(event);
		}
	}
	
	private class DelayedDispatchThread extends Thread {
		
		private Event event;
		private boolean running = true;
		private boolean terminate = false;

		public DelayedDispatchThread(Event event) {
			this.event = event;
			start();
		}
		
		public void terminate() {
			if (running) {
				terminate  = true;
				try {
					join();
				} catch (InterruptedException e) {
				}
			}
		}

		@Override
		public void run() {
			long startTime = System.currentTimeMillis();
			try {
				while (System.currentTimeMillis() - startTime < ONSET_DELAY && !terminate) {
					Thread.sleep(10);
				}
				dispatch(event);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			running = false;
		}
	}
	
	private void dispatch(Event event) {
		event.put("angle", currentAngle);
		super.send(event);
	}
	
	@Override
	public void onBeamAngleChanged(float angle) {
		if (kinect.getKinect() instanceof KinectV1) {
			// Kinect V1 gives the value in degrees
			currentAngle = angle;
		} else {
			// Kinect V2 gives the value in radians
			currentAngle = (float) Math.toDegrees(angle);// + 10;
		}
	}


}
