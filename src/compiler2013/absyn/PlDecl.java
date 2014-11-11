package compiler2013.absyn;

public class PlDecl {
	public String label = "PlDecl";
	public Ty ty;
	public Decltor decltor;
	
	public PlDecl(Ty t, Decltor dtor) {
		ty = t;
		decltor = dtor;
	}
}
