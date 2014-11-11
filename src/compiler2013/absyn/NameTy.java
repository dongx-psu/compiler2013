package compiler2013.absyn;

public class NameTy extends Ty {
	public String label = "NameTy";
	
	public NameTy(compiler2013.symbol.Symbol sym) {
		symbol = sym;
	}
}
