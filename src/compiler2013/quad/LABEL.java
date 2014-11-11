package compiler2013.quad;

import java.util.LinkedHashSet;
import java.util.Set;

import compiler2013.addr.Label;
import compiler2013.analysis.Expression;
import compiler2013.assem.Assem;
import compiler2013.assem.AssemList;

public class LABEL extends Quad {
	public Label label = null;
	
	public LABEL(Label l) {
		label = l;
	}

	public String toString() {
		return label + ":";
	}

	@Override
	public AssemList gen() {
		return L(new Assem("!%:", label));
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
