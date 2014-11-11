package compiler2013.absyn;

public class CharConst extends Expr {
	public String label = "CharConst";
	public char value;
	
	public CharConst(char ch) {
		value = ch;
	}
}
