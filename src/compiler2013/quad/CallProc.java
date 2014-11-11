package compiler2013.quad;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import compiler2013.addr.Label;
import compiler2013.addr.Temp;
import compiler2013.analysis.Expression;
import compiler2013.assem.Assem;
import compiler2013.assem.AssemList;

public class CallProc extends Quad {
	private Label label = null;
	private List<Temp> paras = null;
	
	public CallProc(Label l, List<Temp> p) {
		label = l;
		paras = p;
	}

	public String toString() {
		return "call " + label + " " + paras;
	}
	
	public Set<Temp> use() {
		Set<Temp> set = new LinkedHashSet<Temp>();
		set.addAll(paras);
		return set;
	}
	
	public void replaceUseOf(Temp old, Temp t) {
		List<Temp> newParams = new LinkedList<Temp>();
		for (Temp param: paras) {
			if (param.equals(old)) newParams.add(t);
			else newParams.add(param);
		}
		paras = newParams;
	}

	@Override
	public AssemList gen() {
		return L(saveArgs(), L(storeGlobal(), L(new Assem("jal %", label), loadGlobal())));
	}

	private AssemList loadGlobal() {
		AssemList ans = null;
		for (Temp x: OUT) {
			if (x.global && !x.getLiveInterval().spilled)
				ans = L(ans, L(new Assem("lw @, %($gp)", x, 4 * x.index)));
		}
		return ans;
	}
	
	private AssemList storeGlobal() {
		AssemList ans = null;
		for (Temp x: IN) {
			if (x.global && !x.getLiveInterval().spilled)
				ans = L(ans, L(new Assem("sw %, %($gp)", x, 4 * x.index)));
		}
		return ans;
	}

	private AssemList saveArgs() {
		AssemList ans = null;
		int i = 0;
		while (i < paraRegNum && i < paras.size()) {
			ans = L(ans, L(new Assem("move $%, %", regName[paraRegBase + i], paras.get(i))));
			++i;
		}
		if (i < paras.size()) {
			while (i < paras.size()) {
				ans = L(ans, L(new Assem("sw %, %($v1)", paras.get(i), i * 4)));
				++i;
			}
		}
		return ans;
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
