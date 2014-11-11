package compiler2013.absyn;

import compiler2013.symbol.Symbol;

public class UnionTy extends Ty {
	public static String label = "UnionTy";
	public StructDecls structdecls;
	private static int count = 0;
	
	public UnionTy(StructDecls sds) {
		count++;
		symbol = Symbol.getSymbol("struct !!"+count);
		structdecls = sds;
	}
	
	public UnionTy(compiler2013.symbol.Symbol sym, StructDecls sds) {
		symbol = Symbol.getSymbol("struct " + sym.toString());
		structdecls = sds;
	}
}
