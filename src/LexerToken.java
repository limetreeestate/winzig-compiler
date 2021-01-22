public class LexerToken {
    public final LexerTokenType type;
    public String value;
    public int childCount;

    LexerToken(LexerTokenType t) {
        type = t;
        value = t.val;
    }

}
