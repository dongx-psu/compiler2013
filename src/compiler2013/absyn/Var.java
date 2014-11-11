package compiler2013.absyn;

public class Var extends Expr {
	public String label = "Var";
	public compiler2013.symbol.Symbol symbol;
	
	public Var(compiler2013.symbol.Symbol sym) {
		symbol = sym;
	}
}
