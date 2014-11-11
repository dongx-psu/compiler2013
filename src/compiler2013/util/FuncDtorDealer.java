package compiler2013.util;

import compiler2013.symbol.Symbol;
import compiler2013.type.RECORD;

public final class FuncDtorDealer extends DtorDealer {
	public RECORD agms;
	public boolean extend;
	
	public FuncDtorDealer(Symbol s, int c, RECORD a, boolean flag) {
		name = s;
		ptrcount = c;
		agms = a;
		extend = flag;
	}
}
