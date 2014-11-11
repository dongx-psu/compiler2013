package compiler2013.assem;

public class AssemList {
	public Assem head;
	public AssemList tail;
	
	public AssemList(Assem h, AssemList t) {
		head = h;
		tail = t;
	}
	
	public static AssemList L(Assem h, AssemList t) {
		return new AssemList(h, t);
	}
	
	public static AssemList L(Assem h) {
		return new AssemList(h, null);
	}
	
	public static AssemList L(AssemList a, AssemList b) {
		if (a == null) return b;
		if (b == null) return a;
		return L(a.head, L(a.tail, b));
	}
}
