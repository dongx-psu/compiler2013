package compiler2013.absyn;

public class AdrsExpr extends Expr {
	public String label = "AdrsExpr";
	public Expr ob;
	public Exprs sit;
	
	public AdrsExpr(Expr pfe, Exprs e) {
		ob = pfe;
		sit = e;
	}
}
