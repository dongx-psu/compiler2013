package compiler2013.absyn;

public class SmpPlDecltor extends PlDecltor {
	public String label = "SmpPlDecltor";
	public compiler2013.symbol.Symbol symbol;
	
	public SmpPlDecltor(compiler2013.symbol.Symbol sym) {
		symbol = sym;
	}
}
