package compiler2013.absyn;

public class ReturnStmt extends JmpStmt {
	public String label = "ReturnStmt";	
	public Exprs rtne;
	
	public ReturnStmt(Exprs e) {
		rtne = e;
	}
}
