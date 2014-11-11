package compiler2013.assem;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.LinkedList;

import compiler2013.optimize.AssemOptimize;
import compiler2013.optimize.StoreZeroFolder;
import compiler2013.optimize.StrengthReduction;
import compiler2013.quad.LABEL;
import compiler2013.quad.Quad;
import compiler2013.regalloc.RegAlloc;
import compiler2013.translate.CompileUnit;

public class Codegen {
	private LinkedList<Assem> asms = new LinkedList<Assem>();
	private ArrayList<String> ins = new ArrayList<String>();
	
	public void gen(Assem assem) {
		asms.add(assem);
	}
	
	public void gen(AssemList assems) {
		while (assems != null) {
			gen(assems.head);
			assems = assems.tail;
		}
	}
	
	public void gen(CompileUnit cu, RegAlloc reg) throws Exception {	
		ArrayList<Quad> qs = new ArrayList<Quad>(cu.getQuads());
		ArrayList<Assem> asms = new ArrayList<Assem>();
		boolean flag = false;
		
		StoreZeroFolder.process(qs);
		
		if (((LABEL)qs.get(0)).label.toString() == "main") flag = true;
		
		int i = 0;
		for (Quad q : qs) {
			if (flag && i == 1) asms.add(new Assem("la $v1, args"));
			AssemList p = null;
			p = q.gen();
			while (p != null) {
				asms.add(p.head);
				p = p.tail;
			}
			++i;
		}
		if (flag) {
			asms.add(new Assem("li $v0, 10"));
			asms.add(new Assem("syscall"));
			asms.add(new Assem("jr $ra"));
		}
		
		for (Assem a: asms)
			ins.add(StrengthReduction.process(a).toString(reg));
	}
	
	public void flush(PrintStream out) {
		ArrayList<String> tmpIns = new ArrayList<String>();
		for (String i: ins) {
			while (i.contains("\n")) {
				tmpIns.add(i.substring(0, i.indexOf('\n')));
				i = i.substring(i.indexOf('\n') + 1);
			}
			tmpIns.add(i);
		}
		ins = tmpIns;
		
		ArrayList<String> oldIns = new ArrayList<String>();
		do {
			oldIns.clear();
			oldIns.addAll(ins);
			AssemOptimize.process(ins);
		}
		while (!oldIns.equals(ins));
		
		for (Assem asm : asms)
			out.println(asm);
		for (String i: ins) {
			out.println(i);
		}
	}
}
