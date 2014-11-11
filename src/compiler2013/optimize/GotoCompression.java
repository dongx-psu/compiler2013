package compiler2013.optimize;

import java.util.List;

import compiler2013.addr.Label;
import compiler2013.quad.Goto;
import compiler2013.quad.Branch;
import compiler2013.quad.IfFalse;
import compiler2013.quad.Quad;
import compiler2013.translate.CompileUnit;

public class GotoCompression {
	public static void process(CompileUnit u) {
		List<Quad> qs = u.getQuads();
		for (Quad q : qs) {
			if (q instanceof Goto || q instanceof Branch || q instanceof IfFalse) {
				Quad x = q.jumpTargetIn(qs);
				Quad next = qs.get(qs.indexOf(x) + 1);
				Label l = null;
				while (x != null && next instanceof Goto) {
					l = ((Goto)next).jumpLabel();
					x = next;
					next = qs.get(qs.indexOf(x) + 1);
				}
				
				if (l != null) {
					q.setJumpLabel(l);
				}
			}
		}
	}
}
