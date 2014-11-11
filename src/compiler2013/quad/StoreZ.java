package compiler2013.quad;

import java.util.LinkedHashSet;
import java.util.Set;

import compiler2013.addr.Const;
import compiler2013.addr.Temp;
import compiler2013.analysis.Expression;
import compiler2013.assem.Assem;
import compiler2013.assem.AssemList;

public class StoreZ extends Quad {
	public Temp x;
	public Const y;
	
	public StoreZ(Temp x, Const y) {
		this.x = x;
		this.y = y;
	}
	
	public String toString() {
		return x + "[" + y + "] = 0";
	}
	
	public Set<Temp> use() {
		Set<Temp> set = new LinkedHashSet<Temp>();
		set.add(x);
		return set;
	}
	
	public void replaceUseOf(Temp old, Temp t) {
		if (x.equals(old)) x = t;
	}

	@Override
	public AssemList gen() {
		return L(new Assem("sw $zero, %(%)", y, x));
	}
	
	@Override
	public Set<Expression> genExp() {
		return new LinkedHashSet<Expression>();
	}

	public String toExp() {
		return "(" + x + ")[" + y + "]";
	}
	
	@Override
	public Set<Expression> killExp(Set<Expression> U) {
		return killExpBy(U, toExp());
	}
}
