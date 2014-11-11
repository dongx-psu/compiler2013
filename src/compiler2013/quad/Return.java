package compiler2013.quad;

import java.util.LinkedHashSet;
import java.util.Set;

import compiler2013.addr.Temp;
import compiler2013.analysis.Expression;
import compiler2013.assem.Assem;
import compiler2013.assem.AssemList;

public class Return extends Quad {
	public Temp addr = null;
	
	public Return(Temp a) {
		addr = a;
	}
	
	public String toString() {
		return "return " + addr;
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
		return L(new Assem("move $v0, %", addr));
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
