public enum LexerTokenType {
    /**
     * Represent the tokens of the Scanner and Screener outputs
     * Used as a sequence for parser input
     */
    IDENTIFIER("<identifier>"),
    INTEGER("integer"),
    WHITE_SPACE(" "),
    CHAR("<char>"),
    STRING("<string>"),
    BLOCK_COMMENT,
    INLINE_COMMENT,
    NEWLINE("\n"),                //  \n
    PROGRAM("program"),                //  program
    VAR("var"),                //  var
    CONST("const"),              //  const
    TYPE("type"),               //  type
    FUNCTION("function"),               //  function
    RETURN("return"),             //  return
    BEGIN("begin"),              //  begin
    END("end"),                //  end
    SWAP(":=:"),               //  :=:
    ASSIGN(":="),             //  :=
    OUTPUT("output"),             //  output
    IF("if"),             //  if
    THEN("then"),               //  then
    ELSE("else"),               //  else
    WHILE("while"),              //  while
    DO("do"),             //  do
    CASE("case"),               //  case
    OF("of"),             //  of
    CASE_EXP(".."),               //  ..
    OTHERWISE("otherwise"),              //  otherwise
    REPEAT("repeat"),             //  repeat
    FOR("for"),                //  for
    UNTIL("until"),              //  until
    LOOP("loop"),               //  loop
    POOL("pool"),               //  pool
    EXIT("exit"),               //  exit
    LTE("<="),                //  <=
    NE("<>"),             //  <>
    LT("<"),             //  <
    GTE(">="),                //  >=
    GT(">"),             //  >
    EQ("="),             //  =
    MOD("mod"),                //  mod
    AND("and"),                //  and
    OR("or"),             //  or
    NOT("not"),                //  not
    READ("read"),               //  read
    SUCC("succ"),               //  succ
    PRED("pred"),               //  pred
    CHR("chr"),                //  chr
    ORD("ord"),                //  ord
    EOF("eof"),                //  eof
    COLON(":"),              //  :
    SEMICOLON(";"),              //  ;
    DOT("."),                //  .
    COMMA(","),              //  ,
    LEFT_PARA("("),              //  (
    RIGHT_PARA(")"),             //  )
    BLOCK_BEGIN("{"),              //  {
    BLOCK_END("}"),             //  }
    PLUS("+"),               //  +
    MINUS("-"),              //  -
    MUL("*"),                //  *
    DIV("/"),                //  /
    END_OF_PROGRAM,
    BAD_TOKEN;

    String val;

    LexerTokenType() {}
    LexerTokenType(String s) {
        val = s;
    }

//    public static LexerToken getToken(LexerToken token) {
//        case//
//    }

}