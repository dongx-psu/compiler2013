package compiler2013.type;

import compiler2013.symbol.Symbol;

public final class STRUCT extends RECORD {	
	public STRUCT(Symbol s, Type t, STRUCT next) {
		super(s, t, next);
	}
	
	public int size() {
		if (this.size != -1) return this.size;
		RECORD x = this;
		int ans = 0;
		while (x != null) {
			ans = ans + x.first.type.size();
			x = x.tail;
		}
		this.size = ans;
		return ans;
	}
	
	public boolean eq(Type t) {
		return this.actual() == t.actual();
	}
}
