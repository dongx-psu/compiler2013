package compiler2013.optimize;

import java.util.LinkedHashMap;
import java.util.Map;

import compiler2013.addr.Const;
import compiler2013.addr.Temp;
import compiler2013.analysis.BasicBlock;
import compiler2013.quad.*;

public class LocalCopyPropagation {
	public void process(BasicBlock bb) {
		Map<Temp, Temp> tmap = new LinkedHashMap<Temp, Temp>();
		Map<Temp, Temp> ttmap = new LinkedHashMap<Temp, Temp>();
		Map<Temp, Const> cmap = new LinkedHashMap<Temp, Const>();
		for (Quad q : bb.getQuads()) {
			for (Temp t : q.use()) {
				if (tmap.containsKey(t)) {
					q.replaceUseOf(t, tmap.get(t));
				} else if (cmap.containsKey(t)) {
					if (q instanceof Binop && t.equals(((Binop)q).right))
						((Binop)q).right = cmap.get(t);
					else if (q instanceof Move)
						((Move)q).src = cmap.get(t);
					else if (q instanceof Branch && t.equals(((Branch)q).right))
						((Branch)q).right = cmap.get(t);
				}
			}
			if (q instanceof Move && ((Move) q).src instanceof Temp) {
				tmap.put(((Move) q).dst, (Temp)((Move) q).src);
				ttmap.put((Temp)((Move) q).src, ((Move) q).dst);
				cmap.remove(((Move) q).dst);
			} else if (q instanceof Move && ((Move) q).src instanceof Const) {
				ttmap.remove(tmap.get(((Move) q).dst));
				tmap.remove(((Move) q).dst);
				cmap.put(((Move) q).dst, (Const)((Move) q).src);
			} else if (q instanceof Binop) {
				if (tmap.containsValue(((Binop)q).dst)) {
					tmap.remove(ttmap.get(((Binop) q).dst));
					ttmap.remove(((Binop) q).dst);
				}
				ttmap.remove(tmap.get(((Binop) q).dst));
				tmap.remove(((Binop) q).dst);
				cmap.remove(((Binop) q).dst);
			} else if (q instanceof CallFunc) {
				if (tmap.containsValue(((CallFunc)q).ret)) {
					tmap.remove(ttmap.get(((CallFunc) q).ret));
					ttmap.remove(((CallFunc) q).ret);
				}
				ttmap.remove(tmap.get(((CallFunc) q).ret));
				tmap.remove(((CallFunc) q).ret);
				cmap.remove(((CallFunc) q).ret);
			} else if (q instanceof Load) {
				if (tmap.containsValue(((Load)q).x)) {
					tmap.remove(ttmap.get(((Load) q).x));
					ttmap.remove(((Load) q).x);
				}
				ttmap.remove(tmap.get(((Load) q).x));
				tmap.remove(((Load) q).x);
				cmap.remove(((Load) q).x);
			} else if (q instanceof MoveL) {
				if (tmap.containsValue(((MoveL)q).dst)) {
					tmap.remove(ttmap.get(((MoveL) q).dst));
					ttmap.remove(((MoveL) q).dst);
				}
				ttmap.remove(tmap.get(((MoveL) q).dst));
				tmap.remove(((MoveL) q).dst);
				cmap.remove(((MoveL) q).dst);
			} else if (q instanceof Unaryop) {
				if (tmap.containsValue(((Unaryop)q).dst)) {
					tmap.remove(ttmap.get(((Unaryop) q).dst));
					ttmap.remove(((Unaryop) q).dst);
				}
				ttmap.remove(tmap.get(((Unaryop) q).dst));
				tmap.remove(((Unaryop) q).dst);
				cmap.remove(((Unaryop) q).dst);
			}
		}
	}
}
