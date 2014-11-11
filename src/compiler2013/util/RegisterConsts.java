package compiler2013.util;

public interface RegisterConsts {
	static int savedRegOffset = 2;
	static int savedRegBase = 8;
	static int savedRegNum = 18;
	
	static final int paraRegBase = 4;
	static final int paraRegNum  = 4;
	
	static final String[] regName = {
		"zero", "at",
		"v0", "v1",
		"a0", "a1", "a2", "a3",
		"t0", "t1", "t2", "t3", "t4", "t5", "t6", "t7",
		"s0", "s1", "s2", "s3", "s4", "s5", "s6", "s7",
		"t8", "t9",
		"k0", "k1",
		"gp", "sp", "fp", "ra",
		"spill"
	};
	
	static final int spillReg = regName.length - 1;
}
