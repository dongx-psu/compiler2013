package compiler2013.util;

public interface InsConsts {
	static final String opStr[] = {
		"+", "-", "*", "/", "%", "&", "|", "^", "<<", ">>",
		"&&", "||", "==", "!=", "<", "<=", ">", ">="
		
	};
	
	static final String UopStr[] = {
		"&", "*", "!", "~", "+", "-"
	};
	
	
	static final String[] opAssemStr = {
		"addu", "subu", "mul", "divu", "rem", "and", "or", "xor", "sll", "srl",
		"", "", "seq", "sne", "slt", "sle", "sgt", "sge"
	};
	
	static final String[] opAssemStrI = {
		"addiu", "subu", "mul", "divu", "rem", "andi", "ori", "xori", "sll", "srl",
		"", "", "seq", "sne", "slt", "sle", "sgt", "sge"
	};
}
