package com.fuzzjump.game.util;

/**
 * 
 * @author Steven Galarza
 */
public class TimerUtil {
	
	private long lastTime = System.currentTimeMillis();
	private long waitTime;
	
	public TimerUtil() {
		
	}
	
	public TimerUtil(long initialTime) {
		reset(initialTime);
	}
	
	public boolean elapsed() {
		if (System.currentTimeMillis() - lastTime >= waitTime) {
			lastTime = System.currentTimeMillis();
			waitTime = 0;
			return true;
		}
		return false;
	}
	
	public boolean elapsed(long milliseconds) {
		if (System.currentTimeMillis() - lastTime >= milliseconds) {
			lastTime = System.currentTimeMillis();
			return true;
		}
		return false;
	}
	
	public long timeElapsed() {
		return System.currentTimeMillis() - lastTime;
	}
	
	public void time(long milliseconds) {
		this.waitTime = milliseconds;
		reset(milliseconds);
	}
	
	public void reset(long milliseconds) {
		this.lastTime = System.currentTimeMillis() + milliseconds;
	}
}
