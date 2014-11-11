package compiler2013.absyn;

public class WhileStmt extends IterStmt {
	public String label = "WhileStmt";
	public Exprs cond;
	public Stmt body;
	
	public WhileStmt(Exprs e, Stmt s) {
		cond = e;
		body = s;
	}
}
