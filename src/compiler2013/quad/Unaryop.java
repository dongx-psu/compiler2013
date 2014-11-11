package compiler2013.quad;

import java.util.LinkedHashSet;
import java.util.Set;

import compiler2013.addr.Temp;
import compiler2013.analysis.Expression;
import compiler2013.assem.Assem;
import compiler2013.assem.AssemList;

public class Unaryop extends Quad {
	public Temp dst;
	public Temp src;
	public int op;
	
	public Unaryop(Temp d, int o, Temp s) {
		dst = d;
		src = s;
		op = o;
		if (op == 0) src.flag = true;
	}
	
	public String toString() {
		return dst + " = " + UopStr[op] + " " + src;
	}
	
	public Set<Temp> def() {
		Set<Temp> set = new LinkedHashSet<Temp>();
		set.add(dst);
		return set;
	}
	
	public Set<Temp> use() {
		Set<Temp> set = new LinkedHashSet<Temp>();
		set.add(src);
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
		if (op == 0) {
			if (src.global) return L(new Assem("addiu @, $gp, %", dst, src.index * 4), wb);
			else return L(new Assem("addiu @, $sp, %", dst, src.index * 4), wb);
		} else if (op == 1) {
			return L(new Assem("lw @, 0(%)", dst, src), wb);
		} else if (op == 2) {
			return null;
		} else if (op == 3) {
			return L(new Assem("not @, %", dst, src), wb);
		} else if (op == 4) {
			return L(new Assem("move @, %", dst, src), wb);
		} else if (op == 5) {
			return L(new Assem("neg @, %", dst, src), wb);
		}
		return null;
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
