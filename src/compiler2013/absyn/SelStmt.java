package compiler2013.absyn;

public class SelStmt extends Stmt {
	public String label = "SelStmt";
	public Exprs cond;
	public Stmt act1, act2;
	
	public SelStmt(Exprs e, Stmt s, Stmt x) {
		cond = e;
		act1 = s;
		act2 = x;
	}
}
