package compiler2013.quad;

import java.util.LinkedHashSet;
import java.util.Set;

import compiler2013.addr.Label;
import compiler2013.analysis.Expression;
import compiler2013.assem.Assem;
import compiler2013.assem.AssemList;
import compiler2013.translate.Level;

public class Leave extends Quad {
	private Label label = null;
	private Level level = null;
	
	public Leave(Label l, Level v) {
		label = l;
		level = v;
	}
	
	public String toString() {
		return "leave " + label;
	}

	@Override
	public AssemList gen() {
		int s = level.frameSize();
		return L(level.loadRegs(),
				L(new Assem("lw $ra, %($sp)", s - 4),
				L(new Assem("addiu $sp, $sp, %", s),
				L(new Assem("jr $ra")))));
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
