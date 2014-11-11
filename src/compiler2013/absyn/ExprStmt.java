package compiler2013.absyn;

public class ExprStmt extends Stmt {
	public String label = "ExprStmt";
	public Exprs expr;
	
	public ExprStmt(Exprs e) {
		expr = e;
	}
}
