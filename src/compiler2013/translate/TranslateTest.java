package compiler2013.translate;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import compiler2013.absyn.*;
import compiler2013.addr.Label;
import compiler2013.analysis.Analyzer;
import compiler2013.analysis.BasicBlock;
import compiler2013.assem.Assem;
import compiler2013.assem.Codegen;
import compiler2013.regalloc.LinearScan;
import compiler2013.syntactic.*;
import compiler2013.semantic.*;
import compiler2013.quad.DataFrag;
import compiler2013.quad.Quad;
import compiler2013.optimize.*;

public final class TranslateTest {
	
	private static void semant(String filename) throws Exception {
		InputStream inp = new BufferedInputStream(new FileInputStream(filename));
		Parser parser = new Parser(inp);
		java_cup.runtime.Symbol parseTree = null;
		try {
			parseTree = parser.parse();
		} catch (Throwable e) {
			e.printStackTrace();
			throw new Error(e.toString());
		} finally {
			inp.close();
		}
		
		Decls tree = (Decls) parseTree.value;
		
		Semantic semant = new Semantic();
		semant.checkProg(tree);
		if (semant.hasError()) {
			semant.printErrors();
		}
		else {
			System.out.println("Semant: OK");
			Label.fcount = 0;
			
			Label topLabel = new Label("main");
			Level topLevel = new Level();
			Translate translate = new Translate();
			translate.transProg(tree, topLabel, topLevel);
			List<CompileUnit> units = translate.getUnits();
			
			//=============================== Optimize =================================
			Analyzer analyzer = new Analyzer();
			LabelEliminator le = new LabelEliminator();
			
			for (CompileUnit u : units) {
				//System.out.println(u.getQuads().get(0));
				while (le.eliminate(u));
				u.replaceBranches(analyzer);
				u.findBasicBlocks(analyzer);
				u.findLiveness(analyzer);
			}
			
			LocalCopyPropagation lcp = new LocalCopyPropagation();
			DeadCodeEliminator dce = new DeadCodeEliminator();
	
			for (CompileUnit u : units) {
				List<Quad> oldQuads = new LinkedList<Quad>();
				do {
					oldQuads.clear();
					oldQuads.addAll(u.getQuads());
					
					for (BasicBlock bb : u.getBasicBlocks()) {
						lcp.process(bb);
					}

					u.findLiveness(analyzer);
					dce.process(u);
					u.findBasicBlocks(analyzer);
					
					u.findLiveness(analyzer);
				}
				while (!u.getQuads().equals(oldQuads));
			}
			
			for (CompileUnit u: units)
				GotoCompression.process(u);
			
			//========================================================================
			Codegen codegen = new Codegen();
			codegen.gen(new Assem(".data"));
			codegen.gen(new Assem(".align 2"));
			codegen.gen(new Assem(".globl args"));
			codegen.gen(new Assem("!args:\t.space %", (Translate.maxArgc + 1) * 4));
			codegen.gen(new Assem(".align 2"));
			for (DataFrag df: translate.getDataFrags())
				codegen.gen(df.gen());
			codegen.gen(new Assem(".text"));
			codegen.gen(new Assem(".align 2"));
			codegen.gen(new Assem(".globl main"));
			for (CompileUnit u: units)
				codegen.gen(u, new LinearScan(u, analyzer));/**/

			//=============================== OUTPUT =================================
			for (CompileUnit u : units) {
				System.out.println("########################################");
				System.out.println("############ QUAD & LIVENESS ###########");
				System.out.println("########################################");
				for (Quad q : u.getQuads()) {
					System.out.println(q);
					//System.out.println("IN:" + q.IN + "\tOUT:" + q.OUT);
				}
			}
			
			System.out.println();
			
			PrintStream out = new PrintStream(new BufferedOutputStream(new FileOutputStream("a.s")));
			out.println("########################################");
			out.println("############### CODE GEN ###############");
			out.println("########################################");
			codegen.flush(out);
			
			Scanner scanner = new Scanner(new FileInputStream(new File("runtime.s")));
			while (scanner.hasNextLine()) {
				out.println(scanner.nextLine());
			}
			
			out.close();
		}
	}
	
	public static void main(String argv[]) throws Exception {
			semant("test.c");
	}
}
