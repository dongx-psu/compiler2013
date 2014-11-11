package compiler2013.absyn;

import java.util.LinkedList;

public class Initers {
	public String label = "Initers";
	public LinkedList<Initer> list = new LinkedList<Initer>();
	
	public Initers(Initer inter) {
		list.addFirst(inter);
	}
}
