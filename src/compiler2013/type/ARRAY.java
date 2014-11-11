package compiler2013.type;

public final class ARRAY extends POINTER {
	public int capacity = -1;
	
	public ARRAY(Type t, int c) {
		super(t);
		capacity = c;
		size = c * t.size();
	}
	
	public int size() {
		return size;
	}
	
	public boolean eq(Type t) {
		return this.actual() == t.actual();
	}
}
