package compiler2013.type;

import compiler2013.symbol.Symbol;

public class RECORD extends Type {
	public static class RecordField {
		public Type type;
		public Symbol fieldName;
		
		public RecordField(Symbol s, Type t) {
			type = t;
			fieldName = s;
		}
	}
	
	public RecordField first;
	public RECORD tail;
	
	public RECORD(Symbol s, Type t, RECORD next) {
		first = new RecordField(s, t);
		tail = next;
	}
	
	public int size() {
		if (this.size != -1) return this.size;
		RECORD x = this;
		int ans = 0;
		while (x != null) {
			ans = ans + x.first.type.size();
			x = x.tail;
		}
		this.size = ans;
		return ans;
	}
	
	public boolean eq(Type t) {
		return this.actual() == t.actual();
	}
	
	public RECORD getField(Symbol field) {
		if (first.fieldName == field) return this;
		else if (tail != null) return tail.getField(field);
		else return null;
	}

	public int getFieldIndex(Symbol field) {
		return getFieldIndex(field, 0); 
	}
	
	private int getFieldIndex(Symbol field, int index) {
		if (first.fieldName == field)
			return index;
		if (tail != null)
			return tail.getFieldIndex(field, index + first.type.size());
		return -1;
	}
}
