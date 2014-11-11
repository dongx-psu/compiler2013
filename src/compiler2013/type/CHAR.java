package compiler2013.type;

public final class CHAR extends Type {
	public int size = 1;
	
	public int size() {
		return size;
	}
	
	public boolean eq(Type t) {
		return t.actual() instanceof CHAR;
	}
}
