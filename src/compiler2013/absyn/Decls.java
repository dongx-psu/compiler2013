package compiler2013.absyn;

import java.util.LinkedList;

public class Decls {
	public String label = "Decls";
	public LinkedList<Decl> list = new LinkedList<Decl>();
	
	public Decls(Decl d) {
		list.addFirst(d);
	}
}
