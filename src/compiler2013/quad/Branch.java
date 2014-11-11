package compiler2013.quad;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import compiler2013.addr.Addr;
import compiler2013.addr.Const;
import compiler2013.addr.Label;
import compiler2013.addr.Temp;
import compiler2013.analysis.Expression;
import compiler2013.assem.Assem;
import compiler2013.assem.AssemList;

public class Branch extends Quad {
	public static String[] ifFalseCmp = {"ne", "eq", "ge", "gt", "le", "lt"};
	
	public String cmp;
	public Temp left;
	public Addr right;
	public Label label;
	
	public Branch(String c, Temp l, Addr r, Label lbl) {
		cmp = c;
		left = l;
		right = r;
		label = lbl;
	}
	
	public String toString() {
		if (right instanceof Const && ((Const) right).value == 0) {
			return "b" + cmp + "z " + left + " goto " + label;
		}
		return "b" + cmp + " " + left + " "  + right + " goto " + label;
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
		set.add(left);
		if (right instanceof Temp) set.add((Temp)right);
		return set;
	}
	
	public void replaceUseOf(Temp old, Temp t) {
		if (left.equals(old)) left = t;
		if (right instanceof Temp && right.equals(old)) right = t;
	}

	@Override
	public AssemList gen() {
		if (right instanceof Const && ((Const) right).value == 0)
			return L(new Assem("b%z %, %", cmp, left, label));
		return L(new Assem("b% %, %, %", cmp, left, right, label));
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
