package compiler2013.absyn;

public class FuncDecltor extends Decltor {
	public String label = "FuncDecltor";
	public Para para;
	public boolean extend;
	
	public FuncDecltor(PlDecltor pd, Para pr, boolean flag) {
		pldecltor = pd;
		para = pr;
		extend = flag;
	}
}
