package compiler2013.type;

import compiler2013.symbol.Symbol;

public final class NAME extends Type {
	public Symbol name;
	public int level;
	
	public NAME(Symbol n, int l) {
		name = n;
		level = l;
	}
}
