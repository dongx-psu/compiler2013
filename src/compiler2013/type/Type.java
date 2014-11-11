package compiler2013.type;

public abstract class Type {
	public int size = -1;
	
	public Type actual() {
		return this;
	}
	
	public boolean eq(Type t) {
		return false;
	}
	
	public int size() {
		return size;
	}
	
	public static final Type INT = new INT();
	public static final Type CHAR = new CHAR();
	public static final Type VOID = new VOID();
	public static final Type CHARPTR = new POINTER(CHAR);
	public static final Type VOIDPTR = new POINTER(VOID);
}
