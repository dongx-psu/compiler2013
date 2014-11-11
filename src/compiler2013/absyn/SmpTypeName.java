package compiler2013.absyn;

public class SmpTypeName extends TypeName {
	public String label = "SmpTypeName";
	public Ty type;
	
	public SmpTypeName(Ty t) {
		type = t;
	}
}
