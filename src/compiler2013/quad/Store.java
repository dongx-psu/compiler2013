package compiler2013.quad;

import java.util.LinkedHashSet;
import java.util.Set;

import compiler2013.addr.Const;
import compiler2013.addr.Temp;
import compiler2013.analysis.Expression;
import compiler2013.assem.Assem;
import compiler2013.assem.AssemList;

public class Store extends Quad {
	public Temp x, z;
	public Const y;
	
	public Store(Temp x, Const y, Temp z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public String toString() {
		return x + "[" + y + "] = " + z;
	}
	
	public Set<Temp> use() {
		Set<Temp> set = new LinkedHashSet<Temp>();
		set.add(x);
		set.add(z);
		return set;
	}
	
	public void replaceUseOf(Temp old, Temp t) {
		if (x.equals(old)) x = t;
		if (z.equals(old)) z = t;
	}

	@Override
	public AssemList gen() {
		return L(new Assem("sw %, %(%)", z, y, x));
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
