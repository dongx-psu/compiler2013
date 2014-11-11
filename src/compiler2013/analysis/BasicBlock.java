package compiler2013.analysis;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;

import compiler2013.addr.Temp;
import compiler2013.quad.Quad;

public class BasicBlock {
	public static int cnt = 0;
	private int num = 0;
	
	private LinkedList<Quad> quads = new LinkedList<Quad>();
	private LinkedList<BasicBlock> succ = new LinkedList<BasicBlock>();
	private LinkedList<BasicBlock> prev = new LinkedList<BasicBlock>();
	
	public Set<Temp> IN = new LinkedHashSet<Temp>();
	public Set<Temp> OUT = new LinkedHashSet<Temp>();
	
	public BasicBlock() {
		num = cnt++;
	}
	
	public String toString() {
		return "B" + num;
	}
	
	public void addQuad(Quad q) {
		quads.add(q);
	}
	
	public LinkedList<Quad> getQuads() {
		return quads;
	}
	
	public Quad getLastQuad() {
		return quads.descendingIterator().next();
	}
	
	public Quad getFirstQuad() {
		return quads.get(0);
	}
	
	public void addSucc(BasicBlock x) {
		succ.add(x);
	}
	
	public void addPrev(BasicBlock x) {
		prev.add(x);
	}
	
	public LinkedList<BasicBlock> getSucc() {
		return succ;
	}
	
	public LinkedList<BasicBlock> getPrev() {
		return prev;
	}
	
	public Set<Temp> def() {
		Set<Temp> set = new LinkedHashSet<Temp>();
		Iterator<Quad> iter = quads.descendingIterator();
		while (iter.hasNext()) {
			Quad q = iter.next();
			set.addAll(q.def());
			set.removeAll(q.use());
		}
		return set;
	}
	
	public Set<Temp> use() {
		Set<Temp> set = new LinkedHashSet<Temp>();
		Iterator<Quad> iter = quads.descendingIterator();
		while (iter.hasNext()) {
			Quad q = iter.next();
			set.removeAll(q.def());
			set.addAll(q.use());
		}
		return set;
	}
	
	public Set<Expression> avail = new LinkedHashSet<Expression>();
	public Set<Expression> allExp() {
		Set<Expression> x = new LinkedHashSet<Expression>();
		for (Quad q : quads)
		    add(x, q.genExp());
		return x;
	}
	
	public Set<Expression> genExp() {
		Set<Expression> x = new LinkedHashSet<Expression>();
		for (int i = 0; i < quads.size(); ++i) {
			Quad q = quads.get(i);
			x.addAll(q.genExp());
		}
		return x;
	}
	
	public Set<Expression> killExp(Set<Expression> U) {
		Set<Expression> x = new LinkedHashSet<Expression>();
		for (int i = 0; i < quads.size(); ++i) {
			Quad q = quads.get(i);
			for (Iterator<Expression> iter = x.iterator(); iter.hasNext();) {
				Expression e = iter.next();
				for (Temp tt : q.def()) {
					if (e.exp.equals(q.toExp()) && e.dst.equals(tt))
						iter.remove();
				}
			}
			x.addAll(q.killExp(U));
		}
		return x;
	}
	
	
	
	public void add(Set<Expression> x, Set<Expression> y) {
		for (Expression e : y) {
			boolean flag=false;
			for (Expression ee : x)
				if (e.toString().equals(ee.toString())) {
					flag=true;
					break;
				}
			if (!flag) x.add(e);
		}
	}
}
