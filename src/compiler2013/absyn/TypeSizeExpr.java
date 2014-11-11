package compiler2013.absyn;

public class TypeSizeExpr extends Expr {
	public String label = "TypeSizeExpr";
	public TypeName ty;
	
	public TypeSizeExpr(TypeName t) {
		ty = t;
	}
}
