package compiler2013.syntactic;

%%

%class Lexer
%unicode
%line
%column
%cup
%implements Symbols

%{
	StringBuffer string = new StringBuffer();
	char ch;
	int flag = 0;

	private void err(String message) {
		System.out.println("Scanning error in line " + yyline + ", column " + yycolumn + ": " + message);
	}

	private int octToDec(String s) {
		int ans = 0, x = 1;
		for (int i = s.length() - 1; i >= 1; --i) {
			ans += (s.charAt(i) - '0') * x;
			x *= 8;
		}
		return ans;
	}

	private int hexToDec(String s) {
		int ans = 0, x = 1;
		for (int i = s.length() - 1; i >= 1; --i) {
			if (s.charAt(i) >= '0' && s.charAt(i) <= '9') ans += (s.charAt(i) - '0') * x;
			else if (s.charAt(i) >= 'A' && s.charAt(i) <= 'F') ans += (s.charAt(i) - 'A' + 10) * x;
			else if (s.charAt(i) >= 'a' && s.charAt(i) <= 'f') ans += (s.charAt(i) - 'a' + 10) * x;
			x *= 16;
		}
		return ans;
	}

	private java_cup.runtime.Symbol tok(int kind) {
		return new java_cup.runtime.Symbol(kind, yyline, yycolumn);
	}

	private java_cup.runtime.Symbol tok(int kind, Object value) {
		return new java_cup.runtime.Symbol(kind, yyline, yycolumn, value);
	}
%}

%eofval{
	{
		if (yystate() == YYSTRING) {
			err("String boarder doesn't match");
		}
		return tok(EOF, null);
	}
%eofval}

LineTerm = \n|\r|\r\n
InputCharacter = [^\r\n]
Whitespace = {LineTerm}|[ \t\f]

Comment = {TraditionalComment} | {SingleLineComment} | {DocumentationComment}
TraditionalComment   = "/*" [^*] ~"*/"
SingleLineComment = "//" {InputCharacter}* {LineTerm}
DocumentationComment = "/**" {CommentContent} "*"+ "/"
CommentContent       = ( [^*] | \*+ [^/*] )*
PreProcessor = #[^\n\r]*{LineTerm}

Identifier = [_$a-zA-Z][_$a-zA-Z0-9]*
DecInteger = 0|[1-9][0-9]*
OctInteger = 0[1-7][0-7]*
HexInteger = 0[xX][a-fA-F1-9][a-fA-F0-9]*


%state YYSTRING, YYCHAR

%%

<YYINITIAL> {
	{Comment} { /* skip */ }
	{PreProcessor} {}
	{Whitespace} { /* skip */ }
	
	\" { string.setLength(0); yybegin(YYSTRING); }
	\' { flag = 0; yybegin(YYCHAR); }

	"typedef" { return tok(TYPEDEF); }
	"void" { return tok(VOID); }
	"char" { return tok(CHAR); }
	"int"    { return tok(INT); }
	"struct" { return tok(STRUCT); }
	"union" { return tok(UNION); }
	"if"     { return tok(IF); }
	"else" { return tok(ELSE); }
	"while"	{ return tok(WHILE); }
	"for" { return tok(FOR); }
	"continue" { return tok(CONTINUE); }
	"break" { return tok(BREAK); }
	"return" { return tok(RETURN); }
	"sizeof" { return tok(SIZEOF); }


	"(" { return tok(LPAREN); }
	")" { return tok(RPAREN); }
	"{" { return tok(LBRACE); }
	"}" { return tok(RBRACE); }
	"[" { return tok(LBRAKET); }
	"]" { return tok(RBRAKET); }
	"," { return tok(COMMAR); }
	";" { return tok(SEMICOLON); }
	"." { return tok(DOT); }

	"+" { return tok(PLUS); }
	"-" { return tok(MINUS); }
	"*" { return tok(TIMES); }
	"/" { return tok(DIVIDE); }
	"%" { return tok(MOD); }
	"=" { return tok(ASSIGN); }
	"!" { return tok(NOT); }
	"~" { return tok(TIDLE); }
	"<"  { return tok(LT); }
	">"  { return tok(GT); }
	"|" { return tok(OR); }
	"&" { return tok(AND); }
	"^" { return tok(XOR); }

	"||" { return tok(PARAOR); }
	"&&" { return tok(PARAAND); }
	"==" { return tok(EQ); }
	"!=" { return tok(NE) ;}
	"<=" { return tok(LE); }
	">=" { return tok(GE); }
	"<<" { return tok(SHL); }
	">>" { return tok(SHR); }
	"++" { return tok(INC); }
	"--" { return tok(DEC); }
	"->" { return tok(PTR); }
	"..." { return tok(ELLIPSIS); }
	"*=" { return tok(MUL_ASSIGN); }
	"/=" { return tok(DIV_ASSIGN); }
	"%=" { return tok(MOD_ASSIGN); }
	"+=" { return tok(ADD_ASSIGN); }
	"-=" { return tok(SUB_ASSIGN); }
	"<<=" { return tok(SHL_ASSIGN); }
	">>=" { return tok(SHR_ASSIGN); }
	"&=" { return tok(AND_ASSIGN); }
	"^=" { return tok(XOR_ASSIGN); }
	"|=" { return tok(OR_ASSIGN); }

	{Identifier} {
		if (ParserTest.isTypeId(yytext())) return tok(TYPEID, yytext());
		else return tok(ID, yytext());
	}
	{DecInteger} { return tok(NUM, new Integer(yytext())); }
	{OctInteger} { return tok(NUM, new Integer(octToDec(yytext()))); }
	{HexInteger} { return tok(NUM, new Integer(hexToDec(yytext()))); }

	[^] { throw new RuntimeException("Illegal character " + yytext() + " in line " + (yyline + 1) + ", column " + (yycolumn + 1)); }
}

<YYSTRING> {
	\" { yybegin(YYINITIAL); return tok(STRING, string.toString()); }
	[^\n\r\t\"\\]+ { string.append(yytext()); }
	\\t { string.append('\t'); }
	\\n { string.append('\n'); }
	\\r { string.append('\r'); }
	\\\" { string.append('\"'); }
	\\\\ { string.append('\\'); }
}

<YYCHAR> {
	\' { yybegin(YYINITIAL); flag = 0; return tok(CHARCONST, ch); }
	[^\'\n\r\t\'\\] {
		if (flag != 0) throw new RuntimeException("Char can hold only one character");
		else {
			ch = yytext().charAt(0); flag = 1;
		}
	}
	\\t {
		if (flag != 0) throw new RuntimeException("Char can hold only one character");
		else {
			ch = '\t'; flag = 1;
		}
	}
	\\n {
		if (flag != 0) throw new RuntimeException("Char can hold only one character");
		else {
			ch = '\n'; flag = 1;
		}
	}
	\\r {
		if (flag != 0) throw new RuntimeException("Char can hold only one character");
		else {
			ch = '\r'; flag = 1;
		}
	}
	\\\' {
		if (flag != 0) throw new RuntimeException("Char can hold only one character");
		else {
			ch = '\''; flag = 1;
		}
	}
	\\\\ {
		if (flag != 0) throw new RuntimeException("Char can hold only one character");
		else {
			ch = '\\'; flag = 1;
		}
	}
}
