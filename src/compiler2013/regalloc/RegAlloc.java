package compiler2013.regalloc;

import java.util.HashSet;
import java.util.LinkedHashSet;

import compiler2013.addr.Temp;
import compiler2013.util.RegisterConsts;

public abstract class RegAlloc implements RegisterConsts {
	public HashSet<Temp> loads = new LinkedHashSet<Temp>();
	//Temp - Register map function
	public abstract String map(Temp temp);
}
