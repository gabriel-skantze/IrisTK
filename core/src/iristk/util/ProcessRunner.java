package iristk.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class ProcessRunner {
	
	private Process proc;
	protected int result;
	private Thread resultThread;
	private boolean running = true;
	private List<ProcessListener> listeners = new ArrayList<ProcessListener>();
	private String cmd;
	
	private void run(OutputStream stdout, OutputStream stderr) {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				//System.out.println("Destroy " + cmd);
				proc.destroy();
			}
		});
		StreamGobbler errorGobbler = new StreamGobbler(proc.getErrorStream(), stderr);
		StreamGobbler outputGobbler = new StreamGobbler(proc.getInputStream(), stdout);
		errorGobbler.start();
		outputGobbler.start();
		resultThread = new Thread() {
			@Override
			public void run() {
				try {
					result = proc.waitFor();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				running = false;
				synchronized (listeners) {
					for (ProcessListener listener : listeners) {
						listener.processDone(result);
					}
				}
			}
		};
		resultThread.start();
	}
	
	public ProcessRunner(String cmd, OutputStream stdout, OutputStream stderr) throws IOException {
		this.cmd = cmd;
		proc = Runtime.getRuntime().exec(cmd);
		run(stdout, stderr);
	}
	
	public ProcessRunner(List<String> cmd, OutputStream stdout, OutputStream stderr, ProcessListener listener, File workingDir) throws IOException {
		if (listener != null)
			addProcessListener(listener);
		ProcessBuilder pb = new ProcessBuilder(cmd);
		if (workingDir != null)
			pb.directory(workingDir);
		proc = pb.start();
		run(stdout, stderr);
	}

	public boolean isRunning() {
		return running;
	}
	
	public void waitFor() {
		try {
			resultThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public int getResult() {
		return result;
	}
	
	public void destroy() {
		proc.destroy();
	}
	
	private class StreamGobbler extends Thread {
		InputStream is;
		PrintStream out;

		StreamGobbler(InputStream is, OutputStream out) {
			this.is = is;
			if (out != null)
				this.out = new PrintStream(out);
		}

		@Override
		public void run() {
			try {
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				String line=null;
				while ( (line = br.readLine()) != null) {
					if (out != null)
						out.println(line);
					synchronized (listeners) {
						for (ProcessListener listener : listeners) {
							listener.processOutput(line);
						}
					}
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();  
			}
		}
	}

	public void addProcessListener(ProcessListener listener) {
		synchronized (listeners) {
			listeners.add(listener);
		}
	}
	
	public static interface ProcessListener {
		void processDone(int result);
		void processOutput(String line);
	}

	public static String eval(String cmd) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ByteArrayOutputStream err = new ByteArrayOutputStream();
		ProcessRunner proc = new ProcessRunner(cmd, out, err);
		proc.waitFor();
		return new String(out.toByteArray());
	}

}
