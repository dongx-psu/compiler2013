package compiler2013.optimize;

import compiler2013.addr.Const;
import compiler2013.assem.Assem;
import compiler2013.util.InsConsts;

public class StrengthReduction implements InsConsts {
	public static Assem process(Assem asm) {
		if (asm.format.equals("% @, %, % #I")) {
			if (asm.paras[0].equals(opAssemStrI[2]) || asm.paras[0].equals(opAssemStrI[3])) {
				assert(asm.paras[3] instanceof Const);
				int n = ((Const) asm.paras[3]).value;
				if (n == 1)
					return new Assem("move @, % #SR", asm.paras[1], asm.paras[2]);
				int m = 0;
				while (n > 1) {
					if (n % 2 == 0) {
						n /= 2;
						++m;
					} else break;
				}
				
				if (n == 1) {
					if (asm.paras[0].equals(opAssemStrI[2])) {
						return new Assem("sll @, %, % #SR", asm.paras[1], asm.paras[2], m);
					} else {
						return new Assem("sra @, %, % #SR", asm.paras[1], asm.paras[2], m);
					}
				}
			}
		}
		return asm;
	}
}
