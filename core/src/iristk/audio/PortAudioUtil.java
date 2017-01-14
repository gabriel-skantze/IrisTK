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
package iristk.audio;

import iristk.project.Launcher;
import iristk.project.Package;
import iristk.system.CorePackage;
import iristk.system.IrisUtils;

import org.slf4j.Logger;

import com.portaudio.PortAudio;

public class PortAudioUtil {
	
	private static Logger logger = IrisUtils.getLogger(PortAudioUtil.class);

	private static Boolean loaded = false;
	private static Boolean running = false;
	private static int startCount = 0;
	
	public static synchronized void initialize() throws Exception {
		if (!loaded) {
			IrisUtils.addCoreLibPath();
			Package pkg = CorePackage.PACKAGE;
			if (Launcher.is64arch()) {
				pkg.loadLib("x64/portaudio_x64.dll");
				pkg.loadLib("x64/jportaudio_x64.dll");
			} else {
				pkg.loadLib("x86/portaudio_x86.dll");
				pkg.loadLib("x86/jportaudio_x86.dll");
			}
			loaded = true;
			PortAudio.initialize();
			running = true;
		}
	}
	
	public static synchronized void restart() {
		if (running) {
			logger.info("Restarting PortAudio");
			startCount++;
			PortAudio.terminate();
			PortAudio.initialize();
		}
	}
	
	public static int getStartCount() {
		return startCount;
	}

	public static synchronized void terminate() {
		logger.info("Terminating PortAudio");
		if (running)
			PortAudio.terminate();
		running = false;
	}
	
}
