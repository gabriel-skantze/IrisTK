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
package iristk.util;

import java.io.*;

public class ProcessThread extends Thread {

	private String cmd;
	private Process process;
	private Integer exitValue = null;
	private ProcessThreadListener listener;

	public ProcessThread(String cmd) {
		this.cmd = cmd;
	}
	
	public void setProcessThreadListener(ProcessThreadListener listener) {
		this.listener = listener;
	}

	public Integer getExitValue() {
		return exitValue;
	}
	
	public boolean isDone() {
		return exitValue != null;
	}
	
	@Override
	public void run() {
		try {
			process = Runtime.getRuntime().exec(cmd);
			Runtime.getRuntime().addShutdownHook(new Thread() {
				@Override
				public void run() {
			        process.destroy();
			    }
			});
			InputStream inputStream = process.getInputStream();
			BufferedReader inputStreamReader = new BufferedReader(new InputStreamReader(inputStream));
			
			while (exitValue == null) {
				try {
				    while (inputStream.available() > 0) {
				    	String line = inputStreamReader.readLine();
				    	if (listener != null) {
				    		listener.newInputLine(line);
				    	}
				    }
				    // Ask the process for its exitValue.  If the process
				    // is not finished, an IllegalThreadStateException
				    // is thrown.  If it is finished, we fall through and
				    // the variable finished is set to true.
				    exitValue = process.exitValue();
		        } catch (IllegalThreadStateException e) {
		        	Thread.currentThread();
					// Sleep a little to save on CPU cycles
		        	Thread.sleep(100);
		        }
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void destroy() {
		process.destroy();
		interrupt();
	}
	
	public static interface ProcessThreadListener {
		
		void newInputLine(String line);
		
	}

	public static boolean kill(String image) {
		try {
			Process kill = Runtime.getRuntime().exec("cmd.exe /C TASKKILL /F /IM " + image);
			Integer exitValue = null;
			while (exitValue == null) {
				try {
				  exitValue = kill.exitValue();
		        } catch (IllegalThreadStateException e) {
		        	try {
						Thread.sleep(100);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
		        }
			}
			if (exitValue == 0)
				return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
}
