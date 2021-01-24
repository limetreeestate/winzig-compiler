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
    NEWLINE("\n"),
    PROGRAM("program"),
    VAR("var"),
    CONST("const"),
    TYPE("type"),
    FUNCTION("function"),
    RETURN("return"),
    BEGIN("begin"),
    END("end"),
    SWAP(":=:"),
    ASSIGN(":="),
    OUTPUT("output"),
    IF("if"),
    THEN("then"),
    ELSE("else"),
    WHILE("while"),
    DO("do"),
    CASE("case"),
    OF("of"),
    CASE_EXP(".."),
    OTHERWISE("otherwise"),
    REPEAT("repeat"),
    FOR("for"),
    UNTIL("until"),
    LOOP("loop"),
    POOL("pool"),
    EXIT("exit"),
    LTE("<="),
    NE("<>"),
    LT("<"),
    GTE(">="),
    GT(">"),
    EQ("="),
    MOD("mod"),
    AND("and"),
    OR("or"),
    NOT("not"),
    READ("read"),
    SUCC("succ"),
    PRED("pred"),
    CHR("chr"),
    ORD("ord"),
    EOF("eof"),
    COLON(":"),
    SEMICOLON(";"),
    DOT("."),
    COMMA(","),
    LEFT_PARA("("),
    RIGHT_PARA(")"),
    BLOCK_BEGIN("{"),
    BLOCK_END("}"),
    PLUS("+"),
    MINUS("-"),
    MUL("*"),
    DIV("/"),
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