import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class Lexer {
    private String program;
    Vector<LexerTokenType> tokenSequence;
    //Lookup table
    private static Map<String, LexerTokenType> tokenLookupTable = new HashMap<String, LexerTokenType>();
    //Populate lookup table at loadtime
    static {
        for (LexerTokenType token: LexerTokenType.values()) {
            if (
                    token == LexerTokenType.IDENTIFIER ||
                    token == LexerTokenType.INTEGER ||
                    token == LexerTokenType.CHAR ||
                    token == LexerTokenType.STRING ||
                    token == LexerTokenType.BLOCK_COMMENT ||
                    token == LexerTokenType.INLINE_COMMENT ||
                    token == LexerTokenType.END_OF_PROGRAM ||
                    token == LexerTokenType.BAD_TOKEN
            )
                continue;
            tokenLookupTable.put(token.val, token);
        }

        String alpha = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ_";
        for(int i = 0; i < alpha.length(); i++) {
            String s = alpha.substring(i, i+1);
            tokenLookupTable.put(s, LexerTokenType.IDENTIFIER);
        }

        String num = "0123456789";
        for(int i = 0; i < num.length(); i++) {
            String s = num.substring(i, i+1);
            tokenLookupTable.put(s, LexerTokenType.INTEGER);
        }

        tokenLookupTable.put("\"", LexerTokenType.STRING);
        tokenLookupTable.put("'", LexerTokenType.CHAR);
        tokenLookupTable.put("{", LexerTokenType.BLOCK_COMMENT);
        tokenLookupTable.put("#", LexerTokenType.INLINE_COMMENT);
        tokenLookupTable.put("\t", LexerTokenType.WHITE_SPACE);
        tokenLookupTable.put("\r", LexerTokenType.WHITE_SPACE);

    }
    public Lexer() {
    }

    public Lexer(String p) {
        this.program = p;
    }

    public ArrayList<LexerToken> scan() throws Exception {
        return scan(this.program);
    }

    public ArrayList<LexerToken> scan(String program) throws Exception {
        int pos = 0;
        ArrayList<LexerToken> scannerTokens = new ArrayList<LexerToken>();
        for (; pos < program.length();) {
            String s = program.substring(pos, pos+1);
            LexerTokenType tokenType = tokenLookupTable.get(s);

            switch (tokenType) {
                case IDENTIFIER:
                    int newPos = getSequenceEnd(program.substring(pos), pos);

                    LexerToken token = new LexerToken(tokenType);
                    token.value = program.substring(pos, newPos);
                    scannerTokens.add(token);
                    pos = newPos;
                    break;

                case INTEGER:
                    newPos = getSequenceEnd(program.substring(pos), pos);
                    int alphaPos = getAlphaPos(program.substring(pos), pos);
                    newPos = Math.min(newPos, alphaPos);

                    token = new LexerToken(tokenType);
                    token.value = program.substring(pos, newPos);
                    scannerTokens.add(token);
                    pos = newPos;
                    break;

                case BLOCK_COMMENT:
                    newPos = program.indexOf(LexerTokenType.BLOCK_END.val, pos) + 1;

                    token = new LexerToken(tokenType);
                    token.value = program.substring(pos, newPos);
                    scannerTokens.add(token);
                    pos = newPos;
                    break;
                case CHAR:
                    newPos = program.indexOf("'", pos+1) + 1;
                    if (newPos - pos > 3)
                        throw new Exception("Wrong char length");

                    token = new LexerToken(tokenType);
                    token.value = program.substring(pos+1, newPos-1);
                    scannerTokens.add(token);
                    pos = newPos;
                    break;

                case STRING:
                    newPos = program.indexOf("\"", pos+1) + 1;

                    token = new LexerToken(tokenType);
                    token.value = program.substring(pos+1, newPos-1);
                    scannerTokens.add(token);
                    pos = newPos;
                    break;

                case COLON: //Check if actually colon or swap or assignment
                    if (program.substring(pos, pos+3).equals(":=:")) {
                        tokenType = LexerTokenType.SWAP;
                        newPos = pos + 3;
                    } else if (program.substring(pos, pos+2).equals(":=")) {
                        tokenType = LexerTokenType.ASSIGN;
                        newPos = pos + 2;
                    } else {
                        newPos = pos + 1;
                    }

                    scannerTokens.add(new LexerToken(tokenType));
                    pos = newPos;
                    break;

                case DOT:
                    if (pos + 1 != program.length() && program.substring(pos, pos+2).equals("..")) {
                        tokenType = LexerTokenType.CASE_EXP;
                        newPos = pos + 2;
                    } else {
                        newPos = pos + 1;
                    }

                    scannerTokens.add(new LexerToken(tokenType));
                    pos = newPos;
                    break;

                case GT:
                    if (program.substring(pos, pos+2).equals(">=")) {
                        tokenType = LexerTokenType.GTE;
                        newPos = pos + 2;
                    } else {
                        newPos = pos + 1;
                    }

                    scannerTokens.add(new LexerToken(tokenType));
                    pos = newPos;
                    break;

                case LT:
                    String lookAhead = program.substring(pos, pos+2);
                    if (lookAhead.equals("<=")) {
                        tokenType = LexerTokenType.LTE;
                        newPos = pos + 2;
                    } else if (lookAhead.equals("<>")) {
                        tokenType = LexerTokenType.NE;
                        newPos = pos + 2;
                    } else {
                        newPos = pos + 1;
                    }

                    scannerTokens.add(new LexerToken(tokenType));
                    pos = newPos;
                    break;

                default:
                    scannerTokens.add(new LexerToken(tokenType));
                    pos++;
                    break;
            }
        }
        return scannerTokens;
    }

    private int getSpecialCharPos(String source, int pos) {
        for (int i = 0; i < program.length(); i++) {
            String s = source.substring(i, i+1);
            if (s.matches("[!@#$%&*()_+=|<>?{}\\[\\]~-]")) {
                pos += i;
                return pos;
            }
        }

        return -1;
    }

    private int getAlphaPos(String source, int pos) {
        for (int i = 0; i < program.length(); i++) {
            String s = source.substring(i, i+1);
            if (s.matches("[a-zA-z]")) {
                pos += i;
                return pos;
            }
        }

        return -1;
    }

    private int getSequenceEnd(String source, int pos) {
        for (int i = 0; i < program.length(); i++) {
            String s = source.substring(i, i+1);
            if (s.matches("[\\s!:.;@#$%&*()_+=|<>?{}\\[\\]~-]")) {
                pos += i;
                return pos;
            }
        }

        return -1;
    }

    public ArrayList<LexerToken> screen(ArrayList<LexerToken> tokens) {
        ArrayList<LexerToken> screenedTokens = new ArrayList<LexerToken>();

        for (LexerToken token: tokens) {
            switch (token.type) {
                case IDENTIFIER:
                    LexerTokenType t = tokenLookupTable.get(token.value);
                    if (t != null && t != LexerTokenType.IDENTIFIER)
                        screenedTokens.add(new LexerToken(t));
                    else
                        screenedTokens.add(token);
                    break;

                case WHITE_SPACE:
                case BLOCK_COMMENT:
                case NEWLINE:
                case INLINE_COMMENT:
                    break;

                default:
                    screenedTokens.add(token);

            }
        }
        for (LexerToken t: screenedTokens) {
            System.out.println(t.value + "<" + t.type + ">");
        }
        return screenedTokens;
    }
}

