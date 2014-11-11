package compiler2013.absyn;

public class AccExpr extends Expr {
	public String label = "AccExpr";
	public AccType atype;
	public Expr expr;
	
	public AccExpr(AccType ty, Expr e) {
		atype = ty;
		expr = e;
	}
	
	public static enum AccType {INC, DEC}
}
