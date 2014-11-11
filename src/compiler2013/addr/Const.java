package compiler2013.addr;

public final class Const implements Addr {
	public int value = 0;
	
	public Const(int v) {
		value = v;
	}
	
	public String toString() {
		return "" + value;
	}
	public boolean equals(Object o) {
		if (o instanceof Const) {
			return value == ((Const )o).value;
		}
		return false;
	}
}
