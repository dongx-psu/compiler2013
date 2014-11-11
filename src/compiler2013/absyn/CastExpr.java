package compiler2013.absyn;

public class CastExpr extends Expr {
	public String label = "CastExpr";
	public TypeName tn;
	public Expr expr;
	
	public CastExpr(TypeName t, Expr e) {
		tn = t;
		expr = e;
	}
}
