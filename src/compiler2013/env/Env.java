package compiler2013.env;

import compiler2013.addr.Label;
import compiler2013.symbol.*;
import compiler2013.type.*;

public final class Env {
	public Table types = null;
	public Table vars= null;
		
	private Symbol symbol(String n) {
		return Symbol.getSymbol(n);
	}
	
	public Env() {
		initTypes();
		initVars();
	}
	
	private void initTypes() {
		types = new Table();
		types.put(symbol("int"), Type.INT);
		types.put(symbol("char"), Type.CHAR);
		types.put(symbol("void"), Type.VOID);
		types.put(symbol("void*"), Type.VOIDPTR);
		types.put(symbol("char*"), Type.CHARPTR);
	}
	
	private void initVars() {
		vars = new Table();
		vars.put(symbol("malloc"), new VarEntry(
				new FUNCTION(new RECORD(symbol("s"), Type.INT, null),
				Type.VOIDPTR, false, new Label("malloc"))));
		vars.put(symbol("strcpy"), new VarEntry(
				new FUNCTION(new RECORD(symbol("s1"), Type.CHARPTR, new RECORD(symbol("s2"), Type.CHARPTR, new RECORD(symbol("size"), Type.INT, null))),
				Type.CHARPTR, false, new Label("strcpy"))));
		vars.put(symbol("printf"), new VarEntry(
				new FUNCTION(new RECORD(symbol("s"), Type.CHARPTR, null),
				Type.INT, true, new Label("printf"))));
	}
	
	public void beginScope() {
		vars.beginScope();
		types.beginScope();
	}
	
	public void endScope() {
		vars.endScope();
		types.endScope();
	}
	
}
