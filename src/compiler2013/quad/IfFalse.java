package compiler2013.quad;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import compiler2013.addr.Label;
import compiler2013.addr.Temp;
import compiler2013.analysis.Expression;
import compiler2013.assem.Assem;
import compiler2013.assem.AssemList;

public class IfFalse extends Quad {
	public Temp addr = null;
	public Label label = null;
	
	public IfFalse(Temp a, Label l) {
		addr = a;
		label = l;
	}
	
	public String toString() {
		return "beqz " + addr + " goto " + label;
	}
	
	public boolean isJump() {
		return true;
	}
	
	public Label jumpLabel() {
		return label;
	}
	
	public void replaceLabelOf(Label old, Label l) {
		if (label.equals(old)) label = l;
	}
	
	public Quad jumpTargetIn(List<Quad> qs) {
		return findTargetIn(qs, label);
	}
	
	public void setJumpLabel(Label x) {
		label = x;
	}
	
	public Set<Temp> use() {
		Set<Temp> set = new LinkedHashSet<Temp>();
		set.add(addr);
		return set;
	}
	
	public void replaceUseOf(Temp old, Temp t) {
		if (addr.equals(old)) addr = t;
	}

	@Override
	public AssemList gen() {
		return L(new Assem("beqz %, %", addr, label));
	}

	@Override
	public Set<Expression> genExp() {
		return new LinkedHashSet<Expression>();
	}

	@Override
	public Set<Expression> killExp(Set<Expression> U) {
		return new LinkedHashSet<Expression>();
	}
}
