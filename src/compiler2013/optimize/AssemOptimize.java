package compiler2013.optimize;

import java.util.ArrayList;

public class AssemOptimize {
	private static boolean checkSelfMove(String s) {
		// check for move $t1, $t1
		if (s.charAt(0) == '\t') s = s.substring(1);
		String[] data = s.split(" ");
		return data.length >= 3 && data[0].equals("move")
			&& data[1].equals(data[2] + ",");
	}
	
	private static boolean checkSelfLoad(String s) {
		if (s.charAt(0) == '\t') s = s.substring(1);
		// check for lw $t1, 100($t1)
		String[] data = s.split(" ");
		return data.length >= 3 && data[0].equals("lw")
				&& data[2].endsWith("(" + data[1].substring(0, data[1].length()-1) + ")");
	}
	
	private static void removeSelfMoves(ArrayList<String> a) {
		for (int i = 0; i < a.size(); ++i)
			if (checkSelfMove(a.get(i)))
				a.remove(i);
	}
	
	private static void removeCondDefs(ArrayList<String> a) {
		for (int i = 0; i < a.size() - 1; ++i) {
			String x = a.get(i);
			String y = a.get(i + 1);
			if (x.startsWith("\tmove $") && y.startsWith("\tmove $")
				&& x.substring(7, 10).equals(y.substring(7, 10))) {
				a.set(i, y);
				a.remove(i+1);
			} else if (x.startsWith("\tli $") && y.startsWith("\tli $")
				&& x.substring(5, 8).equals(y.substring(5, 8))) {
				a.set(i, y);
				a.remove(i+1);
			} else if (x.startsWith("\tlw $") && y.startsWith("\tlw $") && !checkSelfLoad(y)
					&& x.substring(5, 8).equals(y.substring(5, 8))) {
				a.set(i, y);
				a.remove(i+1);
			} else if (x.startsWith("\tmove $") && y.startsWith("\tlw $") && !checkSelfLoad(y)
					&& x.substring(7, 10).equals(y.substring(5, 8))) {
				a.set(i, y);
				a.remove(i+1);
			} else if (x.startsWith("\tlw $") && y.startsWith("\tmove $")
					&& x.substring(5, 8).equals(y.substring(7, 10))) {
				a.set(i, y);
				a.remove(i+1);
			} else if (x.startsWith("\tli $") && y.startsWith("\tlw $") && !checkSelfLoad(y)
					&& x.substring(5, 8).equals(y.substring(5, 8))) {
				a.set(i, y);
				a.remove(i+1);
			}
		}
	}
	
	public static void process(ArrayList<String> a) {
		removeSelfMoves(a);
		removeCondDefs(a);
	}
}
