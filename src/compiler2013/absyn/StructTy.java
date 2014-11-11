package compiler2013.absyn;

import compiler2013.symbol.Symbol;

public class StructTy extends Ty {
	public static String label = "StructTy";
	public StructDecls structdecls;
	private static int count = 0;
	
	public StructTy(StructDecls sds) {
		count++;
		symbol = Symbol.getSymbol("struct !!"+count);
		structdecls = sds;
	}
	
	public StructTy(compiler2013.symbol.Symbol sym, StructDecls sds) {
		symbol = Symbol.getSymbol("struct " + sym.toString());
		structdecls = sds;
	}
	
	
}
