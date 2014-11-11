package compiler2013.absyn;

public class Op extends Expr {
	public String label = "Op";
	public OpType opType;
	public Expr left, right;
	
	public Op(Expr l, Expr r, OpType t) {
		left = l;
		right = r;
		opType = t;
	}
	
	public static enum OpType {
		ASSIGN, MUL_ASSIGN, DIV_ASSIGN, MOD_ASSIGN, ADD_ASSIGN,
		SUB_ASSIGN, SHL_ASSIGN, SHR_ASSIGN, AND_ASSIGN, XOR_ASSIGN,
		OR_ASSIGN, PARAOR, PARAAND, OR, XOR, AND, EQ, NE, LT, GT, LE,
		GE, PLUS, MINUS, TIMES, DIVIDE, MOD, SHL, SHR
	}
	
}
