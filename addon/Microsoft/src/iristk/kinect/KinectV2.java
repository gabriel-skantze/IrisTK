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

import iristk.speech.windows.MicrosoftPackage;
import iristk.system.IrisUtils;

import org.slf4j.Logger;

import iristk.net.kinect.*;
import iristk.net.kinect2.*;
import iristk.project.Launcher;
import net.sf.jni4net.Bridge;

public class KinectV2 extends Kinect2Wrapper implements IKinect {

	private static Logger logger = IrisUtils.getLogger(KinectV2.class);

	static {
		try {
			//TODO: see if we can remove Kinect20.Face.dll and NuiDatabase from distribution and point to this instead:
			//IrisUtils.addJavaLibPath(new File("C:\\Program Files\\Microsoft SDKs\\Kinect\\v2.0_1409\\Redist\\Face\\x86"));
			Launcher.addJavaLibPath(MicrosoftPackage.PACKAGE.getLibPath());
			String arch = Launcher.is64arch() ? "x64" : "x86"; 
			Launcher.addJavaLibPath(MicrosoftPackage.PACKAGE.getLibPath(arch));
			Bridge.init();
			Bridge.LoadAndRegisterAssemblyFrom(MicrosoftPackage.PACKAGE.getLibPath("IrisTK.Net.Kinect.j4n.dll"));
			Bridge.LoadAndRegisterAssemblyFrom(MicrosoftPackage.PACKAGE.getLibPath(arch + "/IrisTK.Net.Kinect2.j4n.dll"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private String id;

	public KinectV2(String id) {
		logger.info("Initializing Kinect v2");
		this.id = id;
		super.start();
	}
	
	public KinectV2() {
		this("kinect");
	}
	
	@Override
	public String getId() {
		return id;
	}

	@Override
	public void addDepthFrameListener(DepthFrameListener listener) {
		// TODO Auto-generated method stub
	}

	@Override
	public void addSensorElevationListener(SensorElevationListener listener) {
		// TODO Auto-generated method stub
	}

	@Override
	public float getVerticalFOV() {
		return 60;
	}

	@Override
	public float getHorizontalFOV() {
		return 70;
	}

	@Override
	public int getCameraViewHeight() {
		return 1080;
	}

	@Override
	public int getCameraViewWidth() {
		return 1920;
	}

}
