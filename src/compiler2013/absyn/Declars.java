package compiler2013.absyn;

import java.util.LinkedList;

public class Declars {
	public String label = "Declars";
	public LinkedList<Declar> list = new LinkedList<Declar>();
	
	public Declars(Declar d) {
		list.addFirst(d);
	}
}
