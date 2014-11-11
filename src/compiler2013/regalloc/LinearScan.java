package compiler2013.regalloc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.ListIterator;
import java.util.TreeSet;

import compiler2013.addr.Temp;
import compiler2013.analysis.Analyzer;
import compiler2013.analysis.LiveInterval;
import compiler2013.translate.CompileUnit;

public class LinearScan extends RegAlloc implements Comparator<LiveInterval> {
	private TreeSet<Integer> freeReg = new TreeSet<Integer>();
	private ArrayList<LiveInterval> live = new ArrayList<LiveInterval>();
	
	public LinearScan(CompileUnit u, Analyzer analyzer) {
		for (int i = 0; i < savedRegNum; ++i)
			freeReg.add(i + savedRegBase);
		
		u.findLiveIntervals(analyzer);
		
		for (LiveInterval i : u.getLiveIntervals()) {
			Collections.sort(live, this);
			ListIterator<LiveInterval> iter = live.listIterator();
			while (iter.hasNext()) {
				LiveInterval j = iter.next();
				if (j.getEd() >= i.getSt())
					break;
				freeReg.add(j.register);
				iter.remove();
			}
			
			if (i.getTemp().flag) {
				//System.out.println(i.getTemp());
				i.spilled = true;
				i.register = spillReg;
			} else if (live.size() == savedRegNum) {
				LiveInterval spill = live.get(live.size() - 1);
				if (spill.getEd() > i.getEd()) {
					i.register = spill.register;
					spill.spilled = true;
					spill.register = spillReg;
					live.remove(spill);
					live.add(i);
				} else {
					i.spilled = true;
					i.register = spillReg;
				}
			} else {
				i.register = freeReg.pollFirst();
				live.add(i);
			}
		}
		
		for (LiveInterval i : u.getLiveIntervals())
			if (!i.spilled)
				u.getLevel().useReg(i.register);
	}
	
	@Override
	public String map(Temp temp) {
		LiveInterval i = temp.getLiveInterval();
		return "$" + regName[i.register];
	}
	
	@Override
	public int compare(LiveInterval a, LiveInterval b) {
		return a.getEd() - b.getEd();
	}
}
