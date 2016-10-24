package iristk.system;

import iristk.audio.AudioRecorder;
import iristk.flow.FlowLogger;
import iristk.util.NameFilter;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;

public class LoggingModule extends IrisModule {

	private static org.slf4j.Logger slf4jLogger = IrisUtils.getLogger(LoggingModule.class);

	private PrintStream eventLog = null;
	private NameFilter filter;
	private File logDir;
	private boolean logOnSystemStart = false;
	private long logStartTime;
	private List<FlowLogger> flowLoggers = new ArrayList<>();
	
	private List<Logger> loggers = new ArrayList<>();
	private Map<Object, Boolean> logEnabled = new HashMap<>();
	
	private boolean isLogging = false;

	private File currentLogDir;

	private FileAppender fileAppender;

	private boolean respondToActionLoggingEvent = true;

	private List<LoggingTrigger> loggingTriggers = new ArrayList<>();

	public LoggingModule() {
		this(IrisUtils.getTempDir("logs"), NameFilter.ALL, false);
	}

	public LoggingModule(File logDir, NameFilter filter, boolean logOnSystemStart) {
		this.logOnSystemStart = logOnSystemStart;
		this.logDir = logDir;
		this.filter = filter;
		if (!logDir.exists()) {
			logDir.mkdirs();
		}
		AudioRecorder.fixUncompletedLogs(logDir);

		Runtime.getRuntime().addShutdownHook(
				new Thread() {
					@Override
					public void run() {
						stopLogging();
					}
				});
	}	

	public File getLogDir() {
		return logDir;
	}

	public void logOnSystemStart(boolean value) {
		this.logOnSystemStart = value;
	}

	@Override
	public void onEvent(Event event) {
		if (event.triggers("action.logging.start")) {
			if (respondToActionLoggingEvent ) {
				String ts = event.getString("timestamp");
				Long timestamp = null;
				if (ts != null) {
					try {
						timestamp = Long.parseLong(ts);
					} catch (NumberFormatException e) {
						slf4jLogger.error("Bad timestamp: " + ts);
					}
				}
				startLogging(timestamp);
			}
		} else if (event.triggers("action.logging.stop")) {
			if (respondToActionLoggingEvent) {
				stopLogging();
			}
		} else if (filter.accepts(event.getName())) {
			logSystemEvent(event);
		}
		synchronized (this) {
			for (LoggingTrigger trigger : loggingTriggers) {
				if (trigger.startsLogging(event)) {
					startLogging();
				} 
				if (trigger.stopsLogging(event)) {
					stopLogging();
				} 
			}
		}
	}
	
	public void respondToActionLoggingEvent(boolean b) {
		this.respondToActionLoggingEvent = b;
	}
	
	private synchronized void logSystemEvent(Event event) {
		if (eventLog != null) {
			try {
				event.setTime(system.getTimestamp());
				eventLog.write(new String(event.toJSON() + "\n").getBytes());
				eventLog.flush();
			} catch (IOException e) {
				slf4jLogger.error("Problem writing to log", e);
			}
		}
	}

	public synchronized void startLogging() {
		startLogging(null);
	}

	public synchronized void startLogging(Long timestamp) {
		Date date;
		if (timestamp == null)
			date = new Date();
		else
			date = new Date(timestamp);
		String today = new SimpleDateFormat("yyyy-MM-dd").format(date);
		String second = new SimpleDateFormat("HH-mm-ss").format(date);
		currentLogDir = new File(logDir, today + "/" + second);
		currentLogDir.mkdirs();
		logStartTime = System.currentTimeMillis();
		stopLogging();
		try {
			isLogging = true;
			eventLog = new PrintStream(new File(currentLogDir, "events.txt"));

			fileAppender = new FileAppender();
			fileAppender.setName("LoggingModule");
			fileAppender.setFile(new File(currentLogDir, "system.log").getAbsolutePath());
			fileAppender.setLayout(new PatternLayout("%d{HH:mm:ss.SSS}\t%-5p\t%c{1}:%L\t%m%n"));
			fileAppender.setThreshold(Level.INFO);
			fileAppender.activateOptions();
			org.apache.log4j.Logger.getRootLogger().addAppender(fileAppender);
			
			for (Logger logger : loggers) {
				logger.startLogging(currentLogDir);
			}
			
			slf4jLogger.info("Start logging " + today + " " + second);
		} catch (IOException e) {
			slf4jLogger.error("Problem starting log", e);
		} 
	}

	public synchronized void stopLogging() {
		if (isLogging) {
			slf4jLogger.info("Stop logging");
			
			if (eventLog != null) {
				eventLog.close();
				eventLog = null;
			}
			
			if (fileAppender != null) {
				org.apache.log4j.Logger.getRootLogger().removeAppender(fileAppender);
				fileAppender.close();
				fileAppender = null;
			}
			
			for (Logger logger : loggers) {
				try {
					logger.stopLogging();
				} catch (IOException e) {
					slf4jLogger.error("Problem stopping log", e);
				}
			}
			isLogging = false; 
		}
	}

	@Override
	public void init() throws InitializationException {
	}

	public void addLogger(Logger logger) {
		loggers.add(logger);
	}

	@Override
	protected void systemStarted() {
		super.systemStarted();
		if (logOnSystemStart)
			startLogging();
	}

	public boolean isLogging() {
		return isLogging;
	}

	public int getLogTime() {
		return (int) (System.currentTimeMillis() - logStartTime);
	}
	
	public synchronized void addLoggingTrigger(LoggingTrigger trigger) {
		loggingTriggers .add(trigger);
	}

	public static interface LoggingTrigger {

		boolean startsLogging(Event event);

		boolean stopsLogging(Event event);
		
	}
	
}
