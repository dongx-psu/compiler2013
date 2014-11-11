package compiler2013.absyn;

import java.util.LinkedList;

public class Stmts {
	public String label = "Stmts";
	public LinkedList<Stmt>  list = new LinkedList<Stmt>();
	
	public Stmts(Stmt st) {
		list.addFirst(st);
	}
}
