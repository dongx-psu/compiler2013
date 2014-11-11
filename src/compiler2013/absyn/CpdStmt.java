package compiler2013.absyn;

public class CpdStmt extends Stmt {
	public String label = "CpdStmt";
	public Declars ds;
	public Stmts sts;
	
	public CpdStmt(Declars d, Stmts s) {
		ds = d;
		sts = s;
	}
}
