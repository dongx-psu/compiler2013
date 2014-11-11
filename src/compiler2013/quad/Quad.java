package compiler2013.quad;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import compiler2013.addr.Label;
import compiler2013.addr.Temp;
import compiler2013.analysis.Expression;
import compiler2013.assem.Assem;
import compiler2013.assem.AssemList;
import compiler2013.util.InsConsts;
import compiler2013.util.RegisterConsts;

public abstract class Quad implements InsConsts, RegisterConsts {
	
	private boolean leader = false;
	
	private List<Quad> succ = new LinkedList<Quad>();
	
	public Set<Temp> IN = new LinkedHashSet<Temp>();
	public Set<Temp> OUT = new LinkedHashSet<Temp>();
	
	public void clearAll() {
		leader = false;
		succ.clear();
		IN.clear();
		OUT.clear();
	}
	
	public void setLeader() {
		leader = true;
	}
	
	public boolean isLeader() {
		return leader;
	}
	
	public boolean isJump() {
		return false;
	}
	
	public Label jumpLabel() {
		return null;
	}
	
	public void replaceLabelOf(Label old, Label l) {}
	
	public Quad jumpTargetIn(List<Quad> quads) {
		return null;
	}
	
	public Quad findTargetIn(List<Quad> qs, Label target) {
		for (Quad q: qs) {
			/*if (q instanceof LABEL)
				System.out.println(q);*/
			if (q instanceof LABEL && ((LABEL)q).label.equals(target))
				return q;
		}
		return null;
	}
	
	public Set<Temp> def() {
		return new LinkedHashSet<Temp>();
	}
	
	public Set<Temp> use() {
		return new LinkedHashSet<Temp>();
	}
	
	public void replaceUseOf(Temp old, Temp t) {
	}
	
	public void setJumpLabel(Label x) {
	}
	
	public void addSucc(Quad q) {
		succ.add(q);
	}
	
	public List<Quad> getSucc() {
		return succ;
	}

	public abstract AssemList gen();
	
	protected static AssemList L(Assem h, AssemList t) {
		return AssemList.L(h, t);
	}
	
	protected static AssemList L(Assem h) {
		return AssemList.L(h);
	}
	
	protected static AssemList L(AssemList a, AssemList b) {
		return AssemList.L(a, b);
	}
	
	public Set<Expression> avail=new LinkedHashSet<Expression>();
	public abstract Set<Expression> genExp();
	public abstract Set<Expression> killExp(Set<Expression> U);
	
	public String toExp() {
		return "";
	}
	
	protected Set<Expression> killExpBy(Set<Expression> U,Temp t) {
		Set<Expression> x=new LinkedHashSet<Expression>();
		for (Expression e : U)
			if (e.isKilledBy(t)) x.add(e);
		return x;
	}
	
	protected Set<Expression> killExpBy(Set<Expression> U, String s) {
		Set<Expression> x=new LinkedHashSet<Expression>();
		for (Expression e : U)
			if (e.exp.contains(s)) x.add(e);
		return x;
	}
}
