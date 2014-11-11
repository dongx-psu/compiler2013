package compiler2013.optimize;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import compiler2013.translate.CompileUnit;
import compiler2013.addr.Temp;
import compiler2013.quad.*;

public class DeadCodeEliminator {
	public void process(CompileUnit u) {
		List<Quad> result = new LinkedList<Quad>();
		for (Quad q : u.getQuads()) {
			if (q instanceof Binop || q instanceof Load || q instanceof Unaryop || q instanceof Move) {
				Set<Temp> x = new LinkedHashSet<Temp>(q.def());
				boolean flag = true;
				for (Temp t: q.def())
					if (t.global) {
						flag = false;
						break;
					}
				if (flag) x.retainAll(q.OUT);
				if (x.size() == 0) {
					//eliminate this quad
					//System.out.println("dead " + q);
				} else {
					result.add(q);
				}
			}
			else {
				result.add(q);
			}
		}
		u.setQuads(result);
	}
}
