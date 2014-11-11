package compiler2013.optimize;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;

import compiler2013.analysis.BasicBlock;
import compiler2013.analysis.Expression;
import compiler2013.quad.Quad;

public class AvailableExpressionAnalyzer {
	public void analyze(LinkedList<BasicBlock> blocks) {
		Set<Expression> U = new LinkedHashSet<Expression>();
		
		for (BasicBlock b : blocks)
			U.addAll(b.allExp());
		for (BasicBlock b : blocks) {
			b.avail.clear();
			b.avail.addAll(U);
		}
		
		boolean changed = true;
		while (changed) {
			changed = false;
			for (BasicBlock b : blocks) {
				if (b.getPrev().isEmpty()) b.avail.clear();
				else {
					Set<Expression> x = new LinkedHashSet<Expression>(U);
					for (BasicBlock p : b.getPrev())
						retain(x, p.avail);
					remove(x, b.killExp(U));
					add(x, b.genExp());
					
					String s = "";
					String ss = "";
					for (Expression e : b.avail) s += e.toString();
					for (Expression e : x) ss += e.toString();
					if (!s.equals(ss)) {
						b.avail = x;
						changed = true;
					}
				}
			}
		}
		
		for (BasicBlock b : blocks) {
			Quad last = null;
			for (int i = 0; i < b.getQuads().size(); ++i) {
				Quad q = b.getQuads().get(i);
				if (last == null) {
					if (b.getPrev().isEmpty()) q.avail.clear();
					else {
						Set<Expression> x = new LinkedHashSet<Expression>(U);
						for (BasicBlock p : b.getPrev())
							retain(x, p.avail);
						q.avail = x;
					}
				} else {
					q.avail.clear();
					add(q.avail, last.avail);
					remove(q.avail, last.killExp(U));
					add(q.avail, last.genExp());
				}
				last = q;
			}
		}
	}
	
	public void retain(Set<Expression> x, Set<Expression> y) {
		Set<Expression> ans = new LinkedHashSet<Expression>();
		for (Expression e : x) {
			boolean flag = false;
			for (Expression ee : y)
				if (ee.toString().equals(e.toString())) {
					flag = true;
					break;
				}
			if (flag) ans.add(e);
		}
		x.clear();
		x.addAll(ans);
	}
	
	public void remove(Set<Expression> x, Set<Expression> y) {
		Set<Expression> ans = new LinkedHashSet<Expression>();
		for (Expression e : x) {
			boolean flag = false;
			for (Expression ee : y)
				if (ee.toString().equals(e.toString())) {
					flag = true;
					break;
				}
			if (!flag) ans.add(e);
		}
		x.clear();
		x.addAll(ans);
	}
	
	public void add(Set<Expression> x, Set<Expression> y) {
		for (Expression e : y) {
			boolean flag = false;
			for (Expression ee : x)
				if (e.toString().equals(ee.toString())) {
					flag = true;
					break;
				}
			if (!flag) x.add(e);
		}
	}
	
}
