package iristk.util;

public class Timer {

	private long startTime;
	
	public Timer() {
		reset();
	}
	
	public void reset() {
		startTime = System.currentTimeMillis();
	}
	
	public boolean passed(int time) {
		return System.currentTimeMillis() - startTime > time;
	}
	
}
