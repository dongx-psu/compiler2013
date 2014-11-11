package compiler2013.syntactic;

import compiler2013.symbol.Symbol;
import compiler2013.symbol.Table;
import java.io.*;
import com.google.gson.Gson;

final class ParserTest {
	private static Table types = new Table();
	public static boolean mark;
	
	public static void beginScope() {
		types.beginScope();
	}
	
	public static void endScope() {
		types.endScope();
	}
	
	public static boolean isTypeId(String s) {
		//System.out.println(s);
		return ((types.get(Symbol.getSymbol(s))) != null);
	}
	
	public static void addTypeId(String s) {
		//System.out.println("add type " + s);
		types.put(Symbol.getSymbol(s), s);
	}
	
	private static void parse(String filename) throws IOException {
		InputStream inp = new FileInputStream(filename);
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
		Gson gson = new Gson();
		System.out.println(gson.toJson(parseTree.value));
	}

	public static void main(String argv[]) throws IOException {
		parse("test.c");
		//parse(Main.pathOf("example2.c"));
		//parse(Main.pathOf("example3.c"));
	}
}
