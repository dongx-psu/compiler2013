package compiler2013.quad;

import java.util.LinkedHashSet;
import java.util.Set;

import compiler2013.addr.Label;
import compiler2013.analysis.Expression;
import compiler2013.assem.Assem;
import compiler2013.assem.AssemList;

public class DataFrag extends Quad {
	public Label label = null;
	public String value = null;
	
	public DataFrag(Label l, String s) {
		label = l;
		value = s;
	}
	
	public String toString() {
		return label + ":\"" + escape(value) + "\"";
	}
	
	private static String escape(String s) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < s.length(); ++i) {
			char c = s.charAt(i);
			if (c == '\n') buf.append("\\n");
			else if (c == '\t') buf.append("\\t");
			else if (c == '\"') buf.append("\\\"");
			else if (c == '\r') buf.append("\\r");
			else buf.append(c);
		}
		return buf.toString();
	}

	@Override
	public AssemList gen() {
		return L(new Assem("!%:", label),
				L(new Assem(".asciiz \"%\"", escape(value)),
				L(new Assem(".align 2"))));
	}

	@Override
	public Set<Expression> genExp() {
		return new LinkedHashSet<Expression>();
	}

	@Override
	public Set<Expression> killExp(Set<Expression> U) {
		return new LinkedHashSet<Expression>();
	}
} 
