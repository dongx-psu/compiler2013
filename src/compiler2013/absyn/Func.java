package compiler2013.absyn;

public class Func extends Decl {
	public String label = "Func";
	public Ty typespec;
	public PlDecltor pldector;
	public Para para;
	public boolean extend;
	public CpdStmt body;
	
	public Func(Ty ts, PlDecltor pdtor, Para pr, boolean flag, CpdStmt cs) {
		typespec = ts;
		pldector = pdtor;
		para = pr;
		extend = flag;
		body = cs;
	}
}
