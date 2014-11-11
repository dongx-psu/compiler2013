package compiler2013.addr;

import compiler2013.symbol.Symbol;

public final class Label implements Addr {
	public static int count = 0;
	private String name = "";
	public boolean forFunc = false;
	public static int fcount = 0;
	public boolean used = false;
	
	public String toString() {
		return name;
	}
	
	public Label(String n) {
		name = n;
		if (name == "main" || name == "run_main")
			forFunc = true;
	}
	
	public Label() {
		this("L" + (count++));
		forFunc = false;
	}
	
	public Label(Symbol s) {
		this(s.toString());
	}
	
	public static Label forFunction(Symbol name) {
		Label l = new Label();
		l.name = "L" + fcount + "_" + name;
		++fcount;
		l.forFunc = true;
		return l;
	}
	
	public boolean equals(Object o) {
		if (o instanceof Label) {
			return name.equals(((Label) o).name);
		}
		return false;
	}
}
