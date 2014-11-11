package compiler2013.absyn;

public class PlIniter extends Initer {
	public String label = "PlIniter";
	public Expr aexpr;
	
	public PlIniter(Expr e) {
		aexpr = e;
	}
} 
