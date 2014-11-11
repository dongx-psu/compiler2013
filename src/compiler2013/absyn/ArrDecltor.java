package compiler2013.absyn;

public class ArrDecltor extends Decltor {
	public String label = "ArrDecltor";
	public ArrParas arrparas;
	
	public ArrDecltor(PlDecltor pd, ArrParas aprs) {
		pldecltor = pd;
		arrparas = aprs;
	}
}
