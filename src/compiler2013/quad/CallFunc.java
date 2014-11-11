package compiler2013.quad;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import compiler2013.addr.Label;
import compiler2013.addr.Temp;
import compiler2013.analysis.Expression;
import compiler2013.assem.Assem;
import compiler2013.assem.AssemList;

public class CallFunc extends CallProc {
	public Temp ret = null;
	
	public CallFunc(Label l, List<Temp> p, Temp r) {
		super(l, p);
		ret = r;
	}
	
	public String toString() {
		return ret + " = " + super.toString();
	}
	
	public Set<Temp> def() {
		Set<Temp> set = new LinkedHashSet<Temp>();
		set.add(ret);
		return set;
	}
	
	@Override
	public AssemList gen() {
		AssemList wb = null;
		if (ret.global && !OUT.contains(ret)) wb = L(new Assem("sw %, %($gp)", ret, ret.index * 4));
		//else if (ret.user) wb = L(new Assem("sw %, %($sp)", ret, ret.index * 4));
		return L(super.gen(), L(new Assem("move @, $v0", ret), wb));
	}
	
	@Override
	public Set<Expression> genExp() {
		return new LinkedHashSet<Expression>();
	}

	@Override
	public Set<Expression> killExp(Set<Expression> U) {
		return killExpBy(U, ret);
	}
}
