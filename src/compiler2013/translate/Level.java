package compiler2013.translate;

import java.util.TreeSet;

import compiler2013.addr.Temp;
import compiler2013.assem.Assem;
import compiler2013.assem.AssemList;
import compiler2013.util.RegisterConsts;

public class Level implements RegisterConsts {
	public Level parent = null;
	public int depth = 0;
	
	private TreeSet<Integer> usedReg = new TreeSet<Integer>();
	
	public Level() {
		depth = 0;
	}
	
	public Level(Level pl) {
		parent = pl;
		depth = pl.depth + 1;
	}
	
	private int localcount = 0;
	
	public Temp newLocal() {
		boolean flag = false;
		if (this.depth == 0) flag = true;
		else flag = false;
		return new Temp(this, localcount++, flag);
	}

	public int frameSize() {
		return 4 * (savedRegOffset + savedRegNum + localcount);
	}

	public void useReg(int r) {
		usedReg.add(r);
	}
	
	public AssemList saveRegs() {
		AssemList ans = null;
		for (int i = 0; i < savedRegNum; ++i) {
			int r = savedRegBase + i;
			if (usedReg.contains(r)) 
				ans = new AssemList(new Assem("sw $%, %($sp)", regName[r], (localcount + i) * 4), ans);
		}
		return ans;
	}
	
	public AssemList loadRegs() {
		AssemList ans = null;
		for (int i = 0; i < savedRegNum; ++i) {
			int r = savedRegBase + i;
			if (usedReg.contains(r)) 
				ans = new AssemList(new Assem("lw $%, %($sp)", regName[r], (localcount + i) * 4), ans);
		}
		return ans;
	}
}
