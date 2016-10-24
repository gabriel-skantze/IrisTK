package iristk.util;

public abstract class DelayedThread implements Runnable {

	private boolean running = false;
	
	public void invoke(final int delay) {
		if (!running) {
			new Thread() {
				@Override
				public void run() {
					running = true;
					try {
						Thread.sleep(delay);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					DelayedThread.this.run();
					running = false;
				};
			}.start();
		}
	}
	
}
