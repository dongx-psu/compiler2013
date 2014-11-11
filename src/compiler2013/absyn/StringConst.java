package compiler2013.absyn;

public class StringConst extends Expr {
	public String label = "StringConst";
	public String value;
	
	public StringConst(String s) {
		value = s;
	}
}
