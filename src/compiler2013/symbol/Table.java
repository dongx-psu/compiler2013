package compiler2013.symbol;

import compiler2013.util.TableDealer;

class Binder {
	Object value;
	Symbol prevtop;
	Binder tail;
	int level;

	Binder(Object v, Symbol p, Binder t, int l) {
		value = v;
		prevtop = p;
		tail = t;
		level = l;
	}
}

public class Table {

	public java.util.Dictionary<Symbol, Binder> dict = new java.util.Hashtable<Symbol, Binder>();
	private Symbol top = null;
	private Binder marks = null;
	public int level = 0;

	public TableDealer get(Symbol key) {
		Binder e = dict.get(key);
		if (e == null)
			return null;
		else
			return new TableDealer(e.value, e.level);
	}

	public void put(Symbol key, Object value) {
		dict.put(key, new Binder(value, top, dict.get(key), level));
		top = key;
	}

	public void beginScope() {
		marks = new Binder(null, top, marks, level);
		top = null;
		++level;
	}

	public void endScope() {
		while (top != null) {
			Binder e = dict.get(top);
			if (e.tail != null)
				dict.put(top, e.tail);
			else
				dict.remove(top);
			top = e.prevtop;
		}
		top = marks.prevtop;
		marks = marks.tail;
		--level;
	}

	public java.util.Enumeration<Symbol> keys() {
		return dict.keys();
	}
}
