package compiler2013.optimize;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import compiler2013.addr.Label;
import compiler2013.quad.*;
import compiler2013.translate.CompileUnit;

public class LabelEliminator {
	public boolean eliminate(CompileUnit u) {
		List<Quad> qs = new ArrayList<Quad>(u.getQuads());
		for (int i = 0; i < qs.size() - 1; ++i) {
			if (qs.get(i) instanceof LABEL && qs.get(i+1) instanceof LABEL) {
				LABEL q = (LABEL) qs.get(i+1);
				Label l = ((LABEL)qs.get(i)).label;
				//System.out.println(q);
				List<Quad> ans = new LinkedList<Quad>();
				for (Quad quad: u.getQuads()) {
					if (quad != q) {
						quad.replaceLabelOf(q.label, l);
						ans.add(quad);
					}
				}
				u.setQuads(ans);
				return true;
			}
		}
		
		boolean flag = false;
		List<Quad> ans = new LinkedList<Quad>();
		for (Quad q: u.getQuads()) {
			if (q instanceof LABEL) {
				if (isLabelUsed(qs, ((LABEL)q).label))
					ans.add(q);
				else {
					flag = true;
					//System.out.println(q);
				}
			} else ans.add(q);
		}
		if (flag) u.setQuads(ans);
		return flag;
	}

	private boolean isLabelUsed(List<Quad> qs, Label label) {
		if (label.forFunc) return true;
		for (Quad q: qs)
			if (label.equals(q.jumpLabel()))
				return true;
		return false;
	}
}
