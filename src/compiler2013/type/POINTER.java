package compiler2013.type;

public class POINTER extends Type {
	public Type elementType;
	public int size = 4;
	
	public POINTER(Type t) {
		elementType = t;
	}
	
	public int size() {
		return size;
	}
	
	public boolean eq(Type t) {
		return this.actual() == t.actual();
	}
}
