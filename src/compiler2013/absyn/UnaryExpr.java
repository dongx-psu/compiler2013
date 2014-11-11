package compiler2013.absyn;

public class UnaryExpr extends Expr {
	public String label = "UnaryExpr";
	public Expr expr;
	public UnaryType utype;
	
	public UnaryExpr(UnaryType ut, Expr e) {
		expr = e;
		utype = ut;
	}
	
	public static enum UnaryType {
		INC, DEC, AND, TIMES, PLUS, MINUS, TIDLE, NOT, SIZEOF
	}
}
