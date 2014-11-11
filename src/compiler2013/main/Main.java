package compiler2013.main;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import compiler2013.absyn.Decls;
import compiler2013.addr.Label;
import compiler2013.analysis.Analyzer;
import compiler2013.analysis.BasicBlock;
import compiler2013.assem.Assem;
import compiler2013.assem.Codegen;
import compiler2013.optimize.AvailableExpressionAnalyzer;
import compiler2013.optimize.CommonExpressionEliminator;
import compiler2013.optimize.DeadCodeEliminator;
import compiler2013.optimize.GotoCompression;
import compiler2013.optimize.LabelEliminator;
import compiler2013.optimize.LocalCopyPropagation;
import compiler2013.quad.DataFrag;
import compiler2013.quad.MoveL;
import compiler2013.quad.Quad;
import compiler2013.regalloc.LinearScan;
import compiler2013.semantic.Semantic;
import compiler2013.syntactic.Parser;
import compiler2013.translate.CompileUnit;
import compiler2013.translate.Level;
import compiler2013.translate.Translate;

public class Main {
	private static int compile(String filename) throws Exception {
		//long start = System.nanoTime();
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
			return 1;
		} else {
			//System.out.println("Semant: OK");
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
				u.replaceBranches(analyzer);
				u.findBasicBlocks(analyzer);
				u.findLiveness(analyzer);
			}
			
			
			for (CompileUnit u: units) {
				List<Quad> qs = u.getQuads();
				for (Quad q: qs) {
					if (q instanceof MoveL)
						((MoveL)q).src.used = true;
				}
				GotoCompression.process(u);
			}
			
			LocalCopyPropagation lcp = new LocalCopyPropagation();
			DeadCodeEliminator dce = new DeadCodeEliminator();
			AvailableExpressionAnalyzer aea = new AvailableExpressionAnalyzer();
			CommonExpressionEliminator cse = new CommonExpressionEliminator();
			for (CompileUnit u : units) {
				List<Quad> oldQuads = new LinkedList<Quad>();
				//int cnt = 0;
				do {
					oldQuads.clear();
					oldQuads.addAll(u.getQuads());
					
					do {
						u.findBasicBlocks(analyzer);
						aea.analyze(u.getBasicBlocks());
						//System.out.println(++cnt);
					}
					while (cse.eliminate(u));

					for (BasicBlock bb : u.getBasicBlocks()) {
						lcp.process(bb);
					}

					u.findLiveness(analyzer);
					dce.process(u);
					u.findBasicBlocks(analyzer);
					u.findLiveness(analyzer);
					while (le.eliminate(u));
				}
				while (!u.getQuads().equals(oldQuads));
			}
			
			//========================================================================
			Codegen codegen = new Codegen();
			codegen.gen(new Assem(".data"));
			codegen.gen(new Assem(".align 2"));
			codegen.gen(new Assem(".globl args"));
			codegen.gen(new Assem("!args:\t.space %", (Translate.maxArgc + 1) * 4));
			codegen.gen(new Assem(".align 2"));
			for (DataFrag df: translate.getDataFrags())
				if (df.label.used)
					codegen.gen(df.gen());
			
			codegen.gen(new Assem(".text"));
			codegen.gen(new Assem(".align 2"));
			codegen.gen(new Assem(".globl main"));
			
			for (CompileUnit u: units)
				codegen.gen(u, new LinearScan(u, analyzer));/**/

			//System.out.println((System.nanoTime() - start) / 1000000 + " ms");
			//PrintStream out = System.out;
			PrintStream out = new PrintStream(new BufferedOutputStream(new FileOutputStream("assem.s")));
			out.println("########################################");
			out.println("############### CODE GEN ###############");
			out.println("########################################");
			codegen.flush(out);
			out.println("########################################");
			out.println("############# RUNTIME CODE #############");
			out.println("########################################");
			//Scanner scanner = new Scanner(new FileInputStream(new File("runtime.s")));
			//while (scanner.hasNextLine()) {
			//	out.println(scanner.nextLine());
			//}
			//scanner.close();
			out.close();
			return 0;
		}
	}
	
	public static void main(String argv[]) {
		//if (argv.length > 0) {
			try {
				System.exit(compile("simple.c"));
			} catch (Exception e) {
				System.exit(1);
			}
		//}
	}
}
