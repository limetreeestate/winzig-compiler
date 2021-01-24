import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class winzigc {
    public static void main(String[] args) {
        System.out.println(args[2]);
        String programPath = args[1];
        String outputPath = args[3];


        String program;
        try {
            program = readProgram(programPath);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        Lexer lexer = new Lexer(program);
        try {
            ArrayList<LexerToken> scannedTokens = lexer.scan(program);
            ArrayList<LexerToken> screenedTokens = lexer.screen(scannedTokens);

            System.out.println("");
            Parser parser = new Parser(screenedTokens);
            String output = parser.buildAST();

//            System.out.println(output);

            writeToFile(outputPath, output);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private static String readProgram(String filepath) throws IOException {
        String content = null;
        content = Files.lines(Paths.get(filepath)).collect(Collectors.joining(System.lineSeparator()));
        return content;
    }

    private static void writeToFile(String outputFile, String output) throws IOException {
        FileWriter myWriter = new FileWriter(outputFile);
        myWriter.write(output);
        myWriter.close();
    }
}
