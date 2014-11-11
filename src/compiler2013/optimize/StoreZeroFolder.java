package compiler2013.optimize;

import java.util.ArrayList;

import compiler2013.quad.Quad;
import compiler2013.quad.Move;
import compiler2013.quad.Store;
import compiler2013.quad.StoreZ;
import compiler2013.addr.Const;

public class StoreZeroFolder {
	public static void process(ArrayList<Quad> qs) throws Exception {
		for (int i = 0; i < qs.size()-1; i++) {
			Quad q = qs.get(i);
			if (q instanceof Move && (((Move) q).src instanceof Const) &&
					((Const)((Move) q).src).value == 0) {
				Move x = (Move)q;
				if (((Const)x.src).value == 0 && qs.get(i+1) instanceof Store) {
					Store y = (Store) qs.get(i+1);
					if (y.z.equals(x.dst) && !y.OUT.contains(y.z)) {
						qs.set(i, new StoreZ(y.x, y.y));
						qs.remove(i + 1);
					}
				}
			}
		}
	}
}
