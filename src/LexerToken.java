public class LexerToken {
    public final LexerTokenType type;
    public String value;

    LexerToken(LexerTokenType t) {
        type = t;
        value = t.val;
    }

}
