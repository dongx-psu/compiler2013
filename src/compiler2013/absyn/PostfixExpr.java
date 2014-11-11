package compiler2013.absyn;

public class PostfixExpr extends Expr {
	public String label = "PostfixExpr";
	public Expr pfe;
	public compiler2013.symbol.Symbol symbol;
	public PfType pt;
	
	public PostfixExpr(Expr e, compiler2013.symbol.Symbol s, PfType t) {
		pfe = e;
		symbol = s;
		pt = t;
	}
	
	public static enum PfType { DOT, PTR }
}
