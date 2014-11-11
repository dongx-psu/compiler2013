package compiler2013.absyn;

public class ForStmt extends IterStmt {
	public String label = "ForStmt";
	public Exprs expra, exprb, exprc;
	public Stmt stmt;
	
	public ForStmt(Exprs ea, Exprs eb, Exprs ec, Stmt s) {
		expra = ea;
		exprb = eb;
		exprc = ec;
		stmt = s;
	}
}
