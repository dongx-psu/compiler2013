package compiler2013.quad;

import java.util.LinkedHashSet;
import java.util.Set;

import compiler2013.addr.Addr;
import compiler2013.addr.Temp;
import compiler2013.analysis.Expression;
import compiler2013.assem.Assem;
import compiler2013.assem.AssemList;

public class Binop extends Quad {
	public Temp dst;
	public Temp left;
	public Addr right;
	public int op;
	
	public Binop(Temp d, int o, Temp l, Addr r) {
		dst = d;
		op = o;
		left = l;
		right = r;
	}
	
	public String toString() {
		return dst + " = " + left + " " + opStr[op] + " " + right;
	}
	
	public Set<Temp> def() {
		Set<Temp> set = new LinkedHashSet<Temp>();
		set.add(dst);
		return set;
	}
	
	public Set<Temp> use() {
		Set<Temp> set = new LinkedHashSet<Temp>();
		set.add(left);
		if (right instanceof Temp) set.add((Temp)right);
		return set;
	}
	
	public void replaceUseOf(Temp old, Temp t) {
		if (left.equals(old)) left = t;
		if (right.equals(old)) right = t;
	}

	@Override
	public AssemList gen() {
		AssemList wb = null;
		if (dst.global && !OUT.contains(dst)) wb = L(new Assem("sw %, %($gp)", dst, dst.index * 4));
		//else if (dst.user) wb = L(new Assem("sw %, %($sp)", dst, dst.index * 4));
		if (right instanceof Temp) return L(new Assem("% @, %, %", opAssemStr[op], dst, left, right), wb);
		else return L(new Assem("% @, %, % #I", opAssemStrI[op], dst, left, right), wb);
	}

	public String toExp() {
		return "(" + left + ")" + opStr[op] + "(" + right + ")";
	}
	
	@Override
	public Set<Expression> genExp() {
		Set<Expression> x = new LinkedHashSet<Expression>();
		if (!left.equals(dst) && !right.equals(dst))
			x.add(new Expression(toExp(), dst));
		return x;
	}
	
	@Override
	public Set<Expression> killExp(Set<Expression> U) {
		return killExpBy(U, dst);
	}
}
