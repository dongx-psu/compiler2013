package compiler2013.semantic;

import java.io.*;

import compiler2013.absyn.*;
import compiler2013.syntactic.*;

public final class SemanticTest {
	
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
		}
	}
	
	public static void main(String argv[]) throws Exception {
			semant("e:\\example1.c");
	}
}
