/* JFlex example: part of Java language lexer specification */
import java_cup.runtime.*;

%%
%class Scanner
%standalone
%unicode
%cup
%line
%column
 %{
  StringBuffer string = new StringBuffer();

  private Symbol symbol(int type) {
    return new Symbol(type, yyline, yycolumn);
  }
  private Symbol symbol(int type, Object value) {
    System.out.println(value);
    return new Symbol(type, yyline, yycolumn, value);
  }
%}

LineTerminator = \r|\n|\r\n
InputCharacter = [^\r\n]
WhiteSpace     = {LineTerminator} | [ \t\f]

Identifier = [:jletter:] [:jletterdigit:]*

DecIntegerLiteral = 0 | [1-9][0-9]*

FloatLiteral = "."DecIntegerLiteral

%state STRING
%%
 /* keywords */
<YYINITIAL> "self"           { return symbol(sym.SELF,yytext()); }
<YYINITIAL> "void"           { return symbol(sym.VOID,yytext()); }
<YYINITIAL> "result"           { return symbol(sym.RESULT,yytext()); }
<YYINITIAL> "and"           { return symbol(sym.AND,yytext()); }
<YYINITIAL> "bodycontext"           { return symbol(sym.BODYCONTEXT,yytext()); }
<YYINITIAL> "context"           { return symbol(sym.CONTEXT,yytext()); }
<YYINITIAL> "def"           { return symbol(sym.DEF,yytext()); }
<YYINITIAL> "derive"           { return symbol(sym.DERIVE,yytext()); }
<YYINITIAL> "else"           { return symbol(sym.ELSE,yytext()); }
<YYINITIAL> "endif"           { return symbol(sym.ENDIF,yytext()); }
<YYINITIAL> "endpackage"           { return symbol(sym.ENDPACKAGE,yytext()); }
<YYINITIAL> "if"           { return symbol(sym.IF,yytext()); }
<YYINITIAL> "implies"           { return symbol(sym.IMPLIES,yytext()); }
<YYINITIAL> "in"           { return symbol(sym.IN,yytext()); }
<YYINITIAL> "init"           { return symbol(sym.INIT,yytext()); }
<YYINITIAL> "inv"           { return symbol(sym.INV,yytext()); }
<YYINITIAL> "let"           { return symbol(sym.LET,yytext()); }
<YYINITIAL> "not"           { return symbol(sym.NOT,yytext()); }
<YYINITIAL> "or"           { return symbol(sym.OR,yytext()); }
<YYINITIAL> "package"           { return symbol(sym.PACKAGE,yytext()); }
<YYINITIAL> "post"           { return symbol(sym.POST,yytext()); }
<YYINITIAL> "pre"           { return symbol(sym.PRE,yytext()); }
<YYINITIAL> "@pre"           { return symbol(sym.ATPRE,yytext()); }
<YYINITIAL> "static"           { return symbol(sym.STATIC,yytext()); }
<YYINITIAL> "then"           { return symbol(sym.THEN,yytext()); }
<YYINITIAL> "xor"           { return symbol(sym.XOR,yytext()); }
<YYINITIAL> "true"           { return symbol(sym.TRUE,yytext()); }
<YYINITIAL> "false"           { return symbol(sym.FALSE,yytext()); }
<YYINITIAL> ":"           { return symbol(sym.DOUBLEPOINT,yytext()); }
<YYINITIAL> "->"           { return symbol(sym.ARROW,yytext()); }
 <YYINITIAL> {
  /* identifiers */ 
  {Identifier}                   { return symbol(sym.IDENTIFIER,yytext()); }
 
  /* literals */
  {DecIntegerLiteral}            { return symbol(sym.INTEGER_LITERAL,yytext()); }
  {FloatLiteral}            	 { return symbol(sym.FLOAT_LITERAL,yytext()); }
  \'                             { string.setLength(0); yybegin(STRING); }
  \.           { return symbol(sym.POINT,yytext()); }

  /* operators */
  	"="                            { return symbol(sym.EQ,yytext()); }
	"-"								{ return symbol(sym.MINUS,yytext()); }
	"*"								{ return symbol(sym.MULTIPLY,yytext()); }
	"/"								{ return symbol(sym.DIVIDE,yytext()); }
	"<"								{ return symbol(sym.LESSTHAN,yytext()); }
	">"								{ return symbol(sym.GREATERTHAN,yytext()); }
	"<>"							{ return symbol(sym.NOTEQ,yytext()); }
	"<="							{ return symbol(sym.LESSEQTHAN,yytext()); }
	">="							{ return symbol(sym.GREATEREQTHAN,yytext()); }
  	"+"                            { return symbol(sym.PLUS,yytext()); }
  	"("                            { return symbol(sym.LEFTPARENTHESIS,yytext()); }
  	")"                            { return symbol(sym.RIGHTPARENTHESIS,yytext()); }
  	"["                            { return symbol(sym.LEFTBRACK,yytext()); }
  	"]"                            { return symbol(sym.RIGHTBRACK,yytext()); }
  	"{"                            { return symbol(sym.LEFTBRACKET,yytext()); }
  	"}"                            { return symbol(sym.RIGHTBRACKET,yytext()); }
 
  /* whitespace */
  {WhiteSpace}                   { /* ignore */ }
}
 <STRING> {
  \'                             { yybegin(YYINITIAL); 
                                   return symbol(sym.STRING_LITERAL, 
                                   string.toString()); }
  [^\n\r\"\\]+                   { string.append( yytext() ); }
  \\t                            { string.append('\t'); }
  \\n                            { string.append('\n'); }

  \\r                            { string.append('\r'); }
  \\\"                           { string.append('\"'); }
  \\                             { string.append('\\'); }
}
 /* error fallback */
.|\n                             { throw new Error("Illegal character <"+
                                                    yytext()+">"); }