package compiler2013.util;

public final class Error {
	private String message = null;
	
	public Error(String str, boolean silent) {
		message = str;
		
		if (!silent) {
			System.out.println(this);
			System.out.flush();
		}
	}
	
	public String toString() {
		return message;
	}
}
