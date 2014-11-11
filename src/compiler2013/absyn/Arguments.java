package compiler2013.absyn;

import java.util.LinkedList;

public class Arguments {
	public String label = "Arguments";
	public LinkedList<Expr> list = new LinkedList<Expr>();
	
	
	public Arguments(Expr ae) {
		list.addFirst(ae);
	}
}
