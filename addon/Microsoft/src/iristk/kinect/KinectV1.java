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

import iristk.net.kinect.*;
import iristk.project.Launcher;
import iristk.speech.windows.MicrosoftPackage;
import iristk.system.InitializationException;
import net.sf.jni4net.Bridge;

public class KinectV1 extends KinectWrapper implements IKinect {

	static {
		try {
			Launcher.addJavaLibPath(MicrosoftPackage.PACKAGE.getLibPath());
			Bridge.init();
	        Bridge.LoadAndRegisterAssemblyFrom(MicrosoftPackage.PACKAGE.getLibPath("IrisTK.Net.Kinect.j4n.dll"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private String id;
	
	public KinectV1(String id) throws InitializationException {
		this(id, 0);
	}
	
	public KinectV1() throws InitializationException {
		this("kinect");
	}
	
	@Override
	public String getId() {
		return id;
	}

	public KinectV1(String id, int index) throws InitializationException {
		if (index >= super.getSensorCount()) {
			throw new InitializationException("No Kinect v1 with index " + index + " found");
		}
		System.out.println("Initializing Kinect v1 with index " + index);
		this.id = id;
		Runtime.getRuntime().addShutdownHook(new Thread() {
		    @Override
			public void run() {
		    	System.out.println("Shutting down Kinect");
		        KinectV1.this.stop();
		        System.out.println("Kinect shut down");
		    }
		});
		super.start(index);
	}

	@Override
	public float getVerticalFOV() {
		return 43;
	}

	@Override
	public float getHorizontalFOV() {
		return 57;
	}

	@Override
	//TODO: Implement properly
	public float[] mapColorPointToCameraSpace(int x, int y) {
		float depth = 1;
		float cx = (((x - 320f) / 320f) * (float)Math.tan(Math.toRadians(getHorizontalFOV() / 2)) * depth);
		float cy = (((240f - y) / 240f) * (float)Math.tan(Math.toRadians(getVerticalFOV() / 2)) * depth);
		return new float[]{cx, cy, depth};
	}

	@Override
	public void enableDepthMapping() {
	}

	@Override
	public int getCameraViewHeight() {
		return 480;
	}

	@Override
	public int getCameraViewWidth() {
		return 640;
	}

}
