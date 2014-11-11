package compiler2013.quad;

import java.util.LinkedHashSet;
import java.util.Set;

import compiler2013.addr.Const;
import compiler2013.addr.Temp;
import compiler2013.analysis.Expression;
import compiler2013.assem.Assem;
import compiler2013.assem.AssemList;

public class LoadB extends Quad {
	public Temp x, y;
	public Const z;
	
	public LoadB(Temp x, Temp y, Const z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public String toString() {
		return x + " = " + y + "[" + z +"]";
	}
	
	public Set<Temp> def() {
		Set<Temp> set = new LinkedHashSet<Temp>();
		set.add(x);
		return set;
	}
	
	public Set<Temp> use() {
		Set<Temp> set = new LinkedHashSet<Temp>();
		set.add(y);
		return set;
	}
	
	public void replaceUseOf(Temp old, Temp t) {
		if (y.equals(old)) y = t;
	}

	@Override
	public AssemList gen() {
		AssemList wb = null;
		if (x.global && !OUT.contains(x)) wb = L(new Assem("sw %, %($gp)", x, x.index * 4));
		//else if (x.user) wb = L(new Assem("sw %, %($sp)", x, x.index * 4));
		return L(new Assem("lb @, %(%)", x, z, y), wb);
	}
	
	public String toExp() {
		return "(" + y + ")[" + z + "]";
	}
	
	@Override
	public Set<Expression> genExp() {
		Set<Expression> tmp = new LinkedHashSet<Expression>();
		if (!x.equals(y))
			tmp.add(new Expression(toExp(), x));
		return tmp;
	}
	
	@Override
	public Set<Expression> killExp(Set<Expression> U) {
		return killExpBy(U, x);
	}
}
