package compiler2013.quad;

import java.util.LinkedHashSet;
import java.util.Set;

import compiler2013.addr.Label;
import compiler2013.addr.Temp;
import compiler2013.analysis.Expression;
import compiler2013.assem.Assem;
import compiler2013.assem.AssemList;

public class MoveL extends Quad {
	public Temp dst;
	public Label src;
	
	public MoveL(Temp t, Label l) {
		dst = t;
		src = l;
	}
	
	public String toString() {
		return dst + " = " + src;
	}
	public Set<Temp> def() {
		Set<Temp> set = new LinkedHashSet<Temp>();
		set.add(dst);
		return set;
	}

	@Override
	public AssemList gen() {
		AssemList wb = null;
		if (dst.global && !OUT.contains(dst)) wb = L(new Assem("sw %, %($gp)", dst, dst.index * 4));
		//else if (dst.user) wb = L(new Assem("sw %, %($sp)", dst, dst.index * 4));
		return L(new Assem("la @, %", dst, src), wb);
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
