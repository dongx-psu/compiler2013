package compiler2013.absyn;

public class TypeDecl extends Declar {
	public String label = "TypeDecl";
	public Ty type;
	public Decltors decltors;
	
	public TypeDecl(Ty t, Decltors ds) {
		type = t;
		decltors = ds;
	}
}
