package compiler2013.assem;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

import compiler2013.addr.Temp;
import compiler2013.regalloc.DefaultMap;
import compiler2013.regalloc.RegAlloc;
import compiler2013.util.RegisterConsts;

public class Assem implements RegisterConsts {
	public String format;
	public Object[] paras;
	
	public Assem(String format, Object... paras) {
		this.format = format;
		this.paras = paras;
	}
	
	public Set<Temp> def() {
		Set<Temp> ans = new LinkedHashSet<Temp>();
		int now = 0;
		for (int i = 0; i < format.length(); ++i) {
			char c = format.charAt(i);
			if (c == '@' || c == '%') {
				if (c == '@' && paras[now] instanceof Temp)
					ans.add((Temp)paras[now]);
				++now;
			}
		}
		return ans;
	}
	
	public Set<Temp> use() {
		Set<Temp> ans = new LinkedHashSet<Temp>();
		int now = 0;
		for (int i = 0; i < format.length(); ++i) {
			char c = format.charAt(i);
			if (c == '@' || c == '%') {
				if (c == '%' && paras[now] instanceof Temp)
					ans.add((Temp)paras[now]);
				++now;
			}
		}
		return ans;
	}
	
	public String toString() {
		return toString(DefaultMap.getSingleton());
	}
	
	public String toString(RegAlloc reg) {
		return isSpill() ? doSpill(reg) : doNormal(reg);
	}

	private String doNormal(RegAlloc reg) {
		StringBuffer buf = new StringBuffer();
		
		if (format.charAt(0) == '!')
			format = format.substring(1);
		else buf.append('\t');
		
		int now = 0;
		for (int i = 0; i < format.length(); ++i) {
			char c = format.charAt(i);
			if (c == '@' || c == '%') {
				if (paras[now] instanceof Temp) {
					buf.append(reg.map((Temp) paras[now]));
				}
				else buf.append(paras[now]);
				++now;
			} else buf.append(c);
		}
		return buf.toString();
	}

	private String doSpill(RegAlloc reg) {
		StringBuffer front = new StringBuffer();
		StringBuffer rear = new StringBuffer();
		
		TreeSet<Integer> free = new TreeSet<Integer>();
		free.add(26); free.add(27);
		
		for (Temp t: use()) {
			if (t.getLiveInterval().register == spillReg) {
				int r = free.pollFirst();
				t.getLiveInterval().register = r;
				
				if (t.global) front.append("\tlw $" + regName[r] + ", " + 4 * t.index + "($gp)\n");
				else front.append("\tlw $" + regName[r] + ", " + 4 * t.index + "($sp)\n");
			}
		}
		
		for (Temp t: def())
			if (t.getLiveInterval().register == spillReg)
				t.getLiveInterval().register = 27;
		
		String normal = doNormal(reg);
		
		for (Temp t: def())
			if (t.getLiveInterval().spilled && t.getLiveInterval().register != spillReg) {
				int r = t.getLiveInterval().register;
				t.getLiveInterval().register = spillReg;
				
				if (t.global) rear.append("\n\tsw $" + regName[r] + ", " + 4 * t.index + "($gp)");
				else rear.append("\n\tsw $" + regName[r] + ", " + 4 * t.index + "($sp)");
			}
		
		for (Temp t: use())
			if (t.getLiveInterval().spilled && t.getLiveInterval().register != spillReg)
				t.getLiveInterval().register = spillReg;
		
		return front + normal + rear;
	}

	private boolean isSpill() {
		for (Object p: paras) {
			if (p instanceof Temp && ((Temp) p).getLiveInterval().spilled)
				return true;
		}
		return false;
	}
}
