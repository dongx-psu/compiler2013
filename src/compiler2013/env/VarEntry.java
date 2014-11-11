package compiler2013.env;

import compiler2013.addr.Temp;
import compiler2013.translate.Level;
import compiler2013.type.Type;

public class VarEntry implements Entry {
	public Type type;
	public Temp varAddr = null;
	public Level level = null;
	
	public VarEntry(Type t) {
		type = t;
	}

	public VarEntry(Type t, Temp va) {
		type = t;
		varAddr = va;
	}
	
	public VarEntry(Type t, Level l) {
		type = t;
		level = l;
	}
}
