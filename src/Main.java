import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
//        String programPath = args[1];
//        String outputPath = args[2];

        String program;
        try {
            program = readProgram("winzig_test_programs/winzig_01");
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        Lexer lexer = new Lexer(program);
        try {
            ArrayList<LexerToken> scannedTokens = lexer.scan(program);
            ArrayList<LexerToken> screened = lexer.screen(scannedTokens);
            //
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static String readProgram(String filepath) throws IOException {
        String content = null;
        content = Files.lines(Paths.get(filepath)).collect(Collectors.joining(System.lineSeparator()));
        return content;
    }
}
