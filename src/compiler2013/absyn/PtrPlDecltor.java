package compiler2013.absyn;

public class PtrPlDecltor extends PlDecltor {
	public String label = "PtrPlDecltor";
	public PlDecltor pldecltor;
	
	public PtrPlDecltor(PlDecltor pdtor) {
		pldecltor = pdtor;
	}
}
