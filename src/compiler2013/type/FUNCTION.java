package compiler2013.type;

import compiler2013.addr.Label;

public class FUNCTION extends Type {
	public RECORD argumentType;
	public Type returnType;
	public boolean extend;
	public int size = 4;
	public Label label = null;
	//public Level level = null;
	
	public int size() {
		return size;
	}
	
	public FUNCTION(RECORD at, Type rt, boolean flag) {
		argumentType = at;
		returnType = rt;
		extend = flag;
	}
	
	public FUNCTION(RECORD at, Type rt, boolean flag, Label l) {
		argumentType = at;
		returnType = rt;
		extend = flag;
		label = l;
	}
	
	public boolean eq(Type t) {
		return this.actual() == t.actual();
	}
}
