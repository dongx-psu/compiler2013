package compiler2013.absyn;

import java.util.LinkedList;

public class Exprs extends Expr {
	public String label = "Exprs";
	public LinkedList<Expr> list = new LinkedList<Expr>();

	public Exprs(Expr e) {
		list.addFirst(e);
	}
}
