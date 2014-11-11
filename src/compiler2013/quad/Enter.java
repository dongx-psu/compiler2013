package compiler2013.quad;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;

import compiler2013.addr.Label;
import compiler2013.addr.Temp;
import compiler2013.analysis.Expression;
import compiler2013.assem.Assem;
import compiler2013.assem.AssemList;
import compiler2013.translate.Level;

public class Enter extends Quad {
	private Label label = null;
	private Level level = null;
	private LinkedList<Temp> paras = null;
	
	public Enter(Label l, Level v, LinkedList<Temp> p) {
		label = l;
		level = v;
		paras = p;
	}
	
	public String toString() {
		return "enter " + label;
	}
	
	public Set<Temp> def() {
		Set<Temp> set = new LinkedHashSet<Temp>();
		set.addAll(paras);
		return set;
	}

	public AssemList gen() {
		int s = level.frameSize();
		return L(new Assem("addiu $sp, $sp, -%", s),
				L(new Assem("sw $ra %($sp)", s - 4),
				L(level.saveRegs(), L(loadArgs(), loadGlobal()))));
	}

	private AssemList loadGlobal() {
		AssemList ans = null;
		for (Temp x: IN) {
			if (x.global && !x.getLiveInterval().spilled)
				ans = L(ans, L(new Assem("lw @, %($gp)", x, 4 * x.index)));
		}
		return ans;
	}

	private AssemList loadArgs() {
		AssemList ans = null;
		int i = 0;
		while (i < paraRegNum && i < paras.size()) {
			ans = L(ans, L(new Assem("move @, $%", paras.get(i), regName[paraRegBase +  i])));
			++i;
		}
		if (i < paras.size())
			while (i < paras.size()) {
				ans = L(ans, L(new Assem("lw @, %($v1)", paras.get(i), i * 4)));
				++i;
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
