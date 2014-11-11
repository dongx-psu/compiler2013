package compiler2013.quad;

import java.util.LinkedHashSet;
import java.util.Set;

import compiler2013.addr.Addr;
import compiler2013.addr.Temp;
import compiler2013.analysis.Expression;
import compiler2013.assem.Assem;
import compiler2013.assem.AssemList;

public class Move extends Quad {
	public Temp dst = null;
	public Addr src = null;
	
	public Move(Temp d, Addr s) {
		dst = d;
		src = s;
	}
	
	public String toString() {
		return dst + " = " + src;
	}
	
	public Set<Temp> def() {
		Set<Temp> set = new LinkedHashSet<Temp>();
		set.add(dst);
		return set;
	}
	
	public Set<Temp> use() {
		Set<Temp> set = new LinkedHashSet<Temp>();
		if (src instanceof Temp) set.add((Temp)src);
		return set;
	}
	
	public void replaceUseOf(Temp old, Temp t) {
		if (src.equals(old)) src = t;
	}

	@Override
	public AssemList gen() {
		AssemList wb = null;
		if (dst.global && !OUT.contains(dst)) wb = L(new Assem("sw %, %($gp)", dst, dst.index * 4));
		//else if (dst.user) wb = L(new Assem("sw %, %($sp)", dst, dst.index * 4));
		if (src instanceof Temp) return L(new Assem("move @, %", dst, src), wb);
		else return L(new Assem("li @, %", dst, src), wb);
	}
	
	@Override
	public Set<Expression> genExp() {
		return new LinkedHashSet<Expression>();
	}

	@Override
	public Set<Expression> killExp(Set<Expression> U) {
		return killExpBy(U, dst);
	}
}
