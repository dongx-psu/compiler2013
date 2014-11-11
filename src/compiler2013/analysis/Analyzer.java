package compiler2013.analysis;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import compiler2013.addr.Temp;
import compiler2013.quad.*;

public class Analyzer {
	public List<Quad> replaceBranchs(List<Quad> qs) {
		List<Quad> ans = new LinkedList<Quad>(qs);
		
		for (int i = 1; i < ans.size(); ++i) {
			if (ans.get(i) instanceof IfFalse) {
				IfFalse f = (IfFalse) ans.get(i);
				if (ans.get(i - 1) instanceof Binop) {
					Binop b = (Binop) ans.get(i - 1);
					if (f.addr.equals(b.dst) && b.op >= 12) {
						ans.set(i, new Branch(Branch.ifFalseCmp[b.op - 12], b.left, b.right, f.label));
						ans.remove(i - 1);
					}
				}
			}
		}
		
		return ans;
	}
	
	public LinkedList<BasicBlock> getBasicBlocks(List<Quad> qs) {
		qs = new ArrayList<Quad>(qs);
		for (int i = 0; i < qs.size(); ++i) {
			//System.out.println(qs.get(i));
			qs.get(i).clearAll();
		}
		
		for (int i = 0; i < qs.size() - 1; ++i) {
			Quad q = qs.get(i);
			if (!(q instanceof Goto || q instanceof Leave))
				q.addSucc(qs.get(i + 1));
		}
		
		qs.get(0).setLeader();
		if (qs.size() > 1) {
			if (qs.get(1) instanceof Enter)
				qs.get(2).setLeader();
			else qs.get(1).setLeader();
		}
			
		for (Quad q: qs)
			if (q instanceof Leave)
				q.setLeader();
		
		for (int i = 0; i < qs.size() - 1; ++i) {
			Quad q = qs.get(i);
			if (q.isJump()) {
				//System.out.println(q);
				Quad t = q.jumpTargetIn(qs);
				//System.out.println(t);
				t.setLeader();
				q.addSucc(t);
				qs.get(i + 1).setLeader();
			}
		}
		
		
		LinkedList<BasicBlock> blocks = new LinkedList<BasicBlock>();
		BasicBlock now = null;
		for (Quad q : qs) {
			if (q.isLeader()) {
				if (now != null) blocks.add(now);
				now = new BasicBlock();
			}
			now.addQuad(q);
		}
		
		if (now != null) blocks.add(now);
		
		for (BasicBlock b: blocks) {
			Quad last = b.getLastQuad();
			for (Quad q : last.getSucc())
				newEdge(b, findBlockByQuad(blocks, q));
		}
		return blocks;
	}

	private void newEdge(BasicBlock x, BasicBlock y) {
		x.addSucc(y);
		y.addPrev(x);
	}

	private BasicBlock findBlockByQuad(LinkedList<BasicBlock> blocks, Quad q) {
		for (BasicBlock b : blocks) {
			if (b.getFirstQuad().equals(q))
				return b;
		}
		return null;
	}
	
	public void findLiveness(LinkedList<BasicBlock> blocks) {
		LinkedList<BasicBlock> bs = new LinkedList<BasicBlock>();
		Iterator<BasicBlock> iter = blocks.descendingIterator();
		/*BasicBlock x = iter.next();
		if (!(x.getLastQuad() instanceof Leave))
			bs.add(x);*/
		while (iter.hasNext()) {
			bs.add(iter.next());
		}
		
		for (BasicBlock b: bs) {
			b.IN.clear();
			b.OUT.clear();
			for (Quad q: b.getQuads()) {
				q.IN.clear();
				q.OUT.clear();
			}
		}
		
		boolean flag = true;
		while (flag) {
			flag = false;
			for (BasicBlock b: bs) {
				b.OUT.clear();
				for (BasicBlock s: b.getSucc())
					b.OUT.addAll(s.IN);
				
				Set<Temp> oldIN = new LinkedHashSet<Temp>(b.IN);
				b.IN.clear();
				b.IN.addAll(b.OUT);
				b.IN.remove(b.def());
				b.IN.addAll(b.use());
				
				if (!oldIN.equals(b.IN))
					flag = true;
			}
		}
		
		for (BasicBlock b: bs) {
			Iterator<Quad> itr = b.getQuads().descendingIterator();
			Quad last = null;
			while (itr.hasNext()) {
				Quad q = itr.next();
				q.OUT = (last == null) ? b.OUT : last.IN;
				q.IN.addAll(q.OUT);
				q.IN.removeAll(q.def());
				q.IN.addAll(q.use());
				last = q;
			}
		}
	}
	
	public LinkedHashMap<Temp, LiveInterval> findLiveIntervals(List<Quad> qs) {
		for (Quad q : qs) {
			for (Temp t : q.IN) {
				t.clearLiveInterval();
			}
			for (Temp t : q.OUT) {
				t.clearLiveInterval();
			}
			for (Temp t : q.use()) {
				t.clearLiveInterval();
			}
			for (Temp t : q.def()) {
				t.clearLiveInterval();
			}
		}
		
		LinkedHashMap<Temp, LiveInterval> ans = new LinkedHashMap<Temp, LiveInterval>();
		int qcount = 0;
		for (Quad q : qs) {
			++qcount;
			for (Temp t : q.IN) {
				t.expandInterval(qcount);
				ans.put(t, t.getLiveInterval());
			}
			for (Temp t : q.OUT) {
				t.expandInterval(qcount);
				ans.put(t, t.getLiveInterval());
			}
			for (Temp t : q.use()) {
				t.expandInterval(qcount);
				ans.put(t, t.getLiveInterval());
			}
			for (Temp t : q.def()) {
				t.expandInterval(qcount);
				ans.put(t, t.getLiveInterval());
			}
		}
		return ans;
	}
} 
