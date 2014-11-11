package compiler2013.type;

public final class INT extends Type {
	public int size = 4;
	
	public boolean eq(Type t) {
		return t.actual() instanceof INT;
	}
	
	public int size() {
		return size;
	}
}
