package compiler2013.optimize;

import java.util.LinkedList;
import java.util.List;

import compiler2013.translate.CompileUnit;
import compiler2013.quad.*;
import compiler2013.analysis.*;

public class CommonExpressionEliminator {
	public boolean eliminate(CompileUnit u) {
		boolean found = false;
		List<Quad> result = new LinkedList<Quad>();
		for (Quad q : u.getQuads()) {
			Quad e = eliminated(q);
			if (e == null) result.add(q);
			else {
				result.add(e);
				found = true;
				//System.out.println("replace: " + q + " to " + e);
			}
		}
		u.setQuads(result);
		return found;
	}
	private Quad eliminated(Quad q) {
		if (q instanceof Binop) {
			for (Expression e : q.avail)
				if (((Binop) q).toExp().equals(e.exp))
					return new Move(((Binop) q).dst, e.dst);
		} else if (q instanceof Load) {
			for (Expression e : q.avail)
				if (((Load) q).toExp().equals(e.exp))
					return new Move(((Load) q).x, e.dst);
		} else if (q instanceof LoadB) {
			for (Expression e : q.avail)
				if (((LoadB) q).toExp().equals(e.exp))
					return new Move(((LoadB) q).x, e.dst);
		}
		return null;
	}
	
}

