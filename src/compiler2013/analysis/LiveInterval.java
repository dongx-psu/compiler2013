package compiler2013.analysis;

import compiler2013.addr.Temp;
import compiler2013.util.RegisterConsts;

public class LiveInterval implements RegisterConsts, Comparable<LiveInterval> {
	private Temp t = null;
	private int st = 0;
	private int ed = 0;
	public int register = spillReg;
	public boolean spilled = false;
	
	public LiveInterval(Temp tmp, int pos) {
		this(tmp, pos, pos);
	}
	
	public LiveInterval(Temp tmp, int ss, int tt) {
		t = tmp;
		st = ss;
		ed = tt;
	}
	
	public Temp getTemp() {
		return t;
	}
	
	public int getSt() {
		return st;
	}
	
	public int getEd() {
		return ed + 1;
	}
	
	public void insert(int pos) {
		if (pos < st) st = pos;
		if (pos > ed) ed = pos;
	}
	
	public boolean intersectWith(LiveInterval o) {
		return (st <= o.st && o.st <= ed) || (st <= o.ed && o.ed <= ed);
	}
	
	public String toString() {
		return "{interval:[" + st + "," + ed + "],register:" + regName[register] + "}";
	}
	
	@Override
	public int compareTo(LiveInterval o) {
		return st - o.st;
	}

}
