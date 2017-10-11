package iristk.util;

public class Profiler {

	private long startTime;
	private int count = 0;
	private EnumMap<String,Long> points = new EnumMap<>();
	private int reportInterval;

	public Profiler(int reportInterval) {
		this.reportInterval = reportInterval;
	}

	public void done() {
		point("Done");
		count++;
		if (count >= reportInterval) {
			for (String k : points.keySet()) {
				System.out.println(k + " " + Utils.mean(points.get(k)));
			}
			count = 0;
			points.clear();
		}
	}

	public void point(String key) {
		points.add(key, System.currentTimeMillis() - startTime);
	}

	public void start() {
		startTime = System.currentTimeMillis();
	}
	
}
