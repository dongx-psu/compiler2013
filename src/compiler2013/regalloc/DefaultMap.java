package compiler2013.regalloc;

import compiler2013.addr.Temp;

public class DefaultMap extends RegAlloc {

	@Override
	public String map(Temp t) {
		return t.toString();
	}
	
	private static DefaultMap singleton = null;

	public static DefaultMap getSingleton() {
		if (singleton == null) {
			singleton = new DefaultMap();
		}
		return singleton;
	}
}
