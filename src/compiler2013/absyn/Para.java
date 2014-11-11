package compiler2013.absyn;

import java.util.LinkedList;

public class Para {
	public String label = "Para";
	public LinkedList<PlDecl> list = new LinkedList<PlDecl>();
	
	public Para(PlDecl pd) {
		list.addFirst(pd);
	}
}
