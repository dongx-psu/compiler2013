package compiler2013.addr;

import compiler2013.analysis.LiveInterval;
import compiler2013.translate.Level;

public final class Temp implements Addr {
	public static int count = 0;
	public int num = 0;
	
	public Level home = null;
	public int index = 0;
	
	public boolean flag = false;
	public boolean global = false;
	
	private LiveInterval interval = null;
	public String strval = null;
	
	public String toString() {
		return "t" + num;
	}
	
	public Temp(Level h, int i, boolean g) {
		num = count++;
		home = h;
		index = i;
		global = g;
	}
	
	public boolean equals(Object o) {
		if (o instanceof Temp) {
			return num == ((Temp) o).num;
		}
		return false;
	}

	public int getNum() {
		return num;
	}
	
	public void clearLiveInterval() {
		interval = null;
	}
	
	public LiveInterval getLiveInterval() {
		return interval;
	}
	
	public void expandInterval(int qcount) {
		if (interval == null)
			interval = new LiveInterval(this, qcount);
		interval.insert(qcount);
	}
}
