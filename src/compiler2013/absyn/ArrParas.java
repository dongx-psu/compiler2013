package compiler2013.absyn;

import java.util.LinkedList;

public class ArrParas {
	public String label = "ArrParas";
	public LinkedList<Expr> list = new LinkedList<Expr>(); 
	
	public ArrParas(Expr ce) {
		list.addFirst(ce);
	}
}
