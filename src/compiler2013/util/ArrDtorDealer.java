package compiler2013.util;

import java.util.LinkedList;
import java.util.List;

import compiler2013.symbol.Symbol;

public final class ArrDtorDealer extends DtorDealer {
	public int arrLength;
	public List<Integer> parasaddr = new LinkedList<Integer>();
	
	public ArrDtorDealer(Symbol s, int c, int l) {
		ptrcount = c;
		name = s;
		arrLength = l;
	}
}
