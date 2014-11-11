package compiler2013.syntactic;

import static compiler2013.syntactic.Symbols.*;

public final class ScannerTest {

	public static void main(String[] args) throws Exception {
		scan("test.c");
	}
	
	private static void scan(String filename) throws Exception {
		java.io.InputStream fin = new java.io.FileInputStream(filename);
		java_cup.runtime.Scanner lexer = new Lexer(fin);
		java_cup.runtime.Symbol tok = null;
		
		do {
			tok = lexer.next_token();
			if (tok.sym == STRING) {
				System.out.print("<STRING, " + tok.value + ">");
			} else if (tok.sym == CHARCONST) {
				System.out.print("<CHARCONST, " + tok.value + ">");
			} else if (tok.sym == NUM) {
				System.out.print("<NUM, " + tok.value + ">");
			} else if (tok.sym == ID) {
				System.out.print("<ID, " + tok.value + ">");
			} else {
				System.out.print(symnames[tok.sym]);
			}
		} while (tok.sym != EOF);
		fin.close();
	}
	
	static String[] symnames = new String[1000];
	static {
		symnames[TYPEDEF] = "<TYPEDEF>";
		symnames[VOID] = "<VOID>";
		symnames[CHAR] = "<CHAR>";
		symnames[INT] = "<INT>";
		symnames[STRUCT] = "<STRUCT>";
		symnames[UNION] = "<UNION>";
		symnames[IF] = "<IF>";
		symnames[ELSE] = "<ELSE>";
		symnames[WHILE] = "<WHILE>";
		symnames[FOR] = "<FOR>";
		symnames[CONTINUE] = "<CONTINUE>";
		symnames[BREAK] = "<BREAK>";
		symnames[RETURN] = "<RETURN>";
		symnames[SIZEOF] = "<SIZEOF>";
		symnames[LPAREN] = "<LPAREN>";
		symnames[RPAREN] = "<RPAREN>";
		symnames[LBRACE] = "<LBRACE>\n";
		symnames[RBRACE] = "<RBRACE>\n";
		symnames[LBRAKET] = "<LBRAKET>";
		symnames[RBRAKET] = "<RBRAKET>";
		symnames[COMMAR] = "<COMMAR>";
		symnames[SEMICOLON] = "<SEMICOLON>\n";
		symnames[DOT] = "<DOT>";
		symnames[PARAAND] = "<PARAAND>";
		symnames[PARAOR] = "<PARAOR>";
		symnames[EQ] = "<EQ>";
		symnames[NE] = "<NE>";
		symnames[LE] = "<LE>";
		symnames[GE] = "<GE>";
		symnames[LT] = "<LT>";
		symnames[GT] = "<GT>";
		symnames[NOT] = "<NOT>";
		symnames[SHL] = "<SHL>";
		symnames[SHR] = "<SHR>";
		symnames[INC] = "<INC>";
		symnames[DEC] = "<DEC>";
		symnames[PTR] = "<PTR>";
		symnames[ELLIPSIS] = "<ELLIPSIS>";
		symnames[ASSIGN] = "<ASSIGN>";
		symnames[ADD_ASSIGN] = "<ADD_ASSIGN>";
		symnames[MUL_ASSIGN] = "<MUL_ASSIGN>";
		symnames[SUB_ASSIGN] = "<SUB_ASSIGN>";
		symnames[DIV_ASSIGN] = "<DIV_ASSIGN>";
		symnames[MOD_ASSIGN] = "<MOD_ASSIGN>";
		symnames[SHL_ASSIGN] = "<SHL_ASSIGN>";
		symnames[SHR_ASSIGN] = "<SHR_ASSIGN>";
		symnames[AND_ASSIGN] = "<AND_ASSIGN>";
		symnames[OR_ASSIGN] = "<OR_ASSIGN>";
		symnames[XOR_ASSIGN] = "<XOR_ASSIGN>";
		symnames[PLUS] = "<PLUS>";
		symnames[MINUS] = "<MINUS>";
		symnames[TIMES] = "<TIMES>";
		symnames[DIVIDE] = "<DIVIDE>";
		symnames[MOD] = "<MOD>";
		symnames[TIDLE] = "<TIDLE>";
		symnames[AND] = "<AND>";
		symnames[OR] = "<OR>";
		symnames[XOR] = "<XOR>";
		symnames[EOF] = "<EOF>";
	}
}
