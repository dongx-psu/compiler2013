package compiler2013.absyn;

public class PtrTypeName extends TypeName {
	public String label = "PtrTypeName";
	public TypeName typename;
	
	public PtrTypeName(TypeName tn) {
		typename = tn;
	}
}
