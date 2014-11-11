package compiler2013.quad;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import compiler2013.addr.Label;
import compiler2013.analysis.Expression;
import compiler2013.assem.Assem;
import compiler2013.assem.AssemList;

public class Goto extends Quad {
	private Label label = null;
	
	public Goto(Label l) {
		label = l;
	}
	
	public String toString() {
		return "goto " + label;
	}
	
	public boolean isJump() {
		return true;
	}
	
	public Label jumpLabel() {
		return label;
	}
	
	public void setJumpLabel(Label x) {
		label = x;
	}
	
	public void replaceLabelOf(Label old, Label l) {
		if (label.equals(old)) label = l;
	}
	
	public Quad jumpTargetIn(List<Quad> qs) {
		return findTargetIn(qs, label);
	}

	@Override
	public AssemList gen() {
		return L(new Assem("j %", label));
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
