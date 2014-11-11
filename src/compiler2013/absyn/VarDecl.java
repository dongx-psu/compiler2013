package compiler2013.absyn;

public class VarDecl extends Declar {
	public String label = "VarDecl";
	public Ty type;
	public InitDecltors initdecltors;
	
	public VarDecl(Ty t, InitDecltors idtors) {
		type = t;
		initdecltors = idtors;
	}
}
