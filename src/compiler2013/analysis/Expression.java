package compiler2013.analysis;

import compiler2013.addr.Temp;

public class Expression {
	public String exp;
	public Temp dst;
	
	public Expression(String e, Temp d) {
		exp = e;
		dst = d;
	}
	
	public boolean isKilledBy(Temp t) {
		return exp.contains("(" + t + ")");
	}
	
	public String toString() {
		return exp + "=>" + dst;
	}
	
	public boolean equals(Object o) {
		if (!(o instanceof Expression)) return false;
		Expression ex = (Expression)o;
		return (ex.exp.equals(this.exp) && ex.dst.equals(this.dst));
	}
}
