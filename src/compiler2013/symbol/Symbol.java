package compiler2013.symbol;

public class Symbol {

	private String name;
	
	public Symbol(String s) {
		name = s;
	}
	
	public String toString() {
		return name;
	}
	
	private static java.util.Dictionary <String, Symbol> dict = new java.util.Hashtable<String, Symbol>();
	
	public static Symbol getSymbol(String s) {
		String ss = s.intern();
		Symbol sym = dict.get(ss);
		if (null == sym) {
			sym = new Symbol(ss);
			dict.put(ss, sym);
		}
		return sym;
	}
}
