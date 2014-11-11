package compiler2013.type;

import compiler2013.symbol.Symbol;

public final class UNION extends RECORD {
	public UNION(Symbol s, Type t, UNION next) {
		super(s, t, next);
	}
	
	public int size() {
		if (this.size != -1) return this.size;
		RECORD x = this;
		int ans = 0;
		while (x != null) {
			if (ans < x.first.type.size()) ans = x.first.type.size();
			x = x.tail;
		}
		this.size = ans;
		return ans;
	}
	
	public int getFieldIndex(Symbol field) {
		return 0; 
	}
	
	public boolean eq(Type t) {
		return this.actual() == t.actual();
	}
}
