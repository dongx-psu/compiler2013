package compiler2013.util;

import compiler2013.symbol.Symbol;

public final class SmpDtorDealer extends DtorDealer {
	public SmpDtorDealer(Symbol s, int c) {
		ptrcount = c;
		name = s;
	}
}
