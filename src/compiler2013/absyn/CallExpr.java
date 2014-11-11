package compiler2013.absyn;

public class CallExpr extends Expr {
	public String label = "CallExpr";
	public Expr pfe;
	public Arguments args;
	
	public CallExpr(Expr e, Arguments ags) {
		pfe = e;
		args = ags;
	}
}
