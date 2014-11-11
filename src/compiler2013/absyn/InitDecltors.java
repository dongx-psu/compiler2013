package compiler2013.absyn;

import java.util.LinkedList;

public class InitDecltors {
	public String label = "InitDecltors";
	public LinkedList<InitDecltor> list = new LinkedList<InitDecltor>();
	
	public InitDecltors(InitDecltor idtor) {
		list.addFirst(idtor);
	}
}
