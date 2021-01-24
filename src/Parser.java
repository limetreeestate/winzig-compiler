import java.util.ArrayList;
import java.util.Stack;

public class Parser {
    private ArrayList<LexerToken> tokenSequence;
    Stack<ASTNode> stack = new Stack<ASTNode>();
    int pos = 0;
    ASTNode root;

    public Parser(ArrayList<LexerToken> seq) {
        tokenSequence = seq;
    }

    public String buildAST() throws Exception {
        Winzig();

        root = stack.pop();
        return root.traverse(0);
    }

    /**
     * Entry to the program
     * Winzig -> 'program' Name ':' Consts Types Dclns SubProgs Body Name '.'
     *
     * @throws Exception
     */
    private void Winzig() throws Exception {
        read(LexerTokenType.PROGRAM);
        Name();
        read(LexerTokenType.COLON);
        Consts();
        Types();
        Dclns();
        SubProgs();
        Body();
        Name();
        read(LexerTokenType.DOT);

        buildSubtreeFromStack("program", 7);
    }

    /**
     * Body -> 'begin' Statement list ';' 'end'
     *
     * @throws Exception
     */
    private void Body() throws Exception {
        read(LexerTokenType.BEGIN);
        Statement();

        int count = 1;
        while (nextToken().type == LexerTokenType.SEMICOLON) {
            read(LexerTokenType.SEMICOLON);
            Statement();
            count++;
        }
        read(LexerTokenType.END);

        buildSubtreeFromStack("block", count);
    }

    /**
     * Statement    -> Assignment
     *              -> 'output' '(' OutExp list ',' ')'
     *              -> 'if' Expression 'then' Statement ('else' Statement)?
     *              -> 'while' Expression 'do' Statement
     *              -> 'repeat' Statement list ';' 'until' Expression
     *              -> 'for' '(' ForStat ';' ForExp ';' ForStat ')' Statement
     *              -> 'loop' Statement list ';' 'pool'
     *              -> 'case' Expression 'of' Caseclauses OtherwiseClause 'end'
     *              -> 'read' '(' Name list ',' ')'
     *              -> 'exit'
     *              -> 'return' Expression
     *              -> Body
     *              ->
     *
     * @throws Exception
     */
    private void Statement() throws Exception {
        LexerToken next = nextToken();
        switch (nextToken().type) {
            case IDENTIFIER:
                Assignment();
                break;

            case OUTPUT:
                read(LexerTokenType.OUTPUT);
                read(LexerTokenType.LEFT_PARA);
                OutExp();

                int count = 1;
                while (nextToken().type == LexerTokenType.COMMA) {
                    read(LexerTokenType.COMMA);
                    OutExp();
                    count++;
                }
                read(LexerTokenType.RIGHT_PARA);

                buildSubtreeFromStack("output", count);
                break;

            case IF:
                read(LexerTokenType.IF);
                Expression();
                read(LexerTokenType.THEN);
                Statement();

                count = 2;
                if (nextToken().type == LexerTokenType.ELSE) {
                    read(LexerTokenType.ELSE);
                    Statement();
                    count++;
                }

                buildSubtreeFromStack("if", count);
                break;

            case WHILE:
                read(LexerTokenType.WHILE);
                Expression();
                read(LexerTokenType.DO);
                Statement();

                buildSubtreeFromStack("while", 2);
                break;

            case REPEAT:
                read(LexerTokenType.REPEAT);
                Statement();

                count = 1;
                while (nextToken().type == LexerTokenType.SEMICOLON) {
                    read(LexerTokenType.SEMICOLON);
                    Statement();
                    count++;
                }
                read(LexerTokenType.UNTIL);
                Expression();

                buildSubtreeFromStack("repeat", ++count);
                break;

            case FOR:
                read(LexerTokenType.FOR);
                read(LexerTokenType.LEFT_PARA);
                ForStat();
                read(LexerTokenType.SEMICOLON);
                ForExp();
                read(LexerTokenType.SEMICOLON);
                ForStat();
                read(LexerTokenType.RIGHT_PARA);
                Statement();

                buildSubtreeFromStack("for", 4);
                break;

            case LOOP:
                read(LexerTokenType.LOOP);
                Statement();

                count = 1;
                while (nextToken().type == LexerTokenType.SEMICOLON) {
                    read(LexerTokenType.SEMICOLON);
                    Statement();
                    count++;
                }
                read(LexerTokenType.POOL);

                buildSubtreeFromStack("loop", count);
                break;

            case CASE:
                Expression();
                read(LexerTokenType.OF);
                count = 1 + Caseclauses();
                OtherwiseClause();
                read(LexerTokenType.END);

                buildSubtreeFromStack("case", 3);
                break;

            case READ:
                read(LexerTokenType.READ);
                read(LexerTokenType.LEFT_PARA);
                Name();

                count = 1;
                while (nextToken().type == LexerTokenType.COMMA) {
                    read(LexerTokenType.COMMA);
                    Name();
                    count++;
                }
                read(LexerTokenType.RIGHT_PARA);

                buildSubtreeFromStack("read", count);
                break;

            case EXIT:
                read(LexerTokenType.EXIT);

                buildSubtreeFromStack("exit", 0);
                break;

            case RETURN:
                read(LexerTokenType.RETURN);
                Expression();

                buildSubtreeFromStack("return", 1);
                break;

            case BEGIN:
                Body();

            default:
                buildSubtreeFromStack("<null>", 0);
                break;

        }
    }

    /**
     * ForStat  -> Assignment
     *          ->
     *
     * @throws Exception
     */
    private void ForExp() throws Exception {
        if(nextToken().type == LexerTokenType.SEMICOLON){
            buildSubtreeFromStack("true",0);
        } else {
            Expression();
        }
    }

    /**
     * OtherwiseClause  -> 'otherwise' Statement
     * -> ;
     *
     * @throws Exception
     */
    private void OtherwiseClause() throws Exception {
        LexerToken next = nextToken();
        switch (nextToken().type) {
            case OTHERWISE:
                read(LexerTokenType.OTHERWISE);
                Statement();

                buildSubtreeFromStack("otherwise", 1);
                break;

            default:
                break;
        }
    }

    /**
     * Caseclauses-> (Caseclause ';')+;
     * @throws Exception
     */
    private int Caseclauses() throws Exception {
        LexerToken next = nextToken();

        int count = 0;
        do {
            Caseclause();
            read(LexerTokenType.SEMICOLON);
            next = nextToken();
            count++;
        } while (next.type == LexerTokenType.INTEGER || next.type == LexerTokenType.CHAR || next.type == LexerTokenType.IDENTIFIER);

        return count;
    }

    /**
     * Caseclause -> CaseExpression list ',' ':' Statement
     *
     * @throws Exception
     */
    private void Caseclause() throws Exception {
        CaseExpression();

        int count = 1;
        while (nextToken().type == LexerTokenType.COMMA) {
            read(LexerTokenType.COMMA);
            CaseExpression();
            count++;
        }
        read(LexerTokenType.COLON);
        Statement();

        buildSubtreeFromStack("case_clause", ++count);
    }

    /**
     * CaseExpression   -> ConstValue
     *                  -> ConstValue '..' ConstValue
     *
     * @throws Exception
     */
    private void CaseExpression() throws Exception {
        ConstValue();
        if (nextToken().type == LexerTokenType.CASE_EXP) {
            read(LexerTokenType.CASE_EXP);
            ConstValue();

            buildSubtreeFromStack("..", 2);
        }
    }

    /**
     * ForStat  -> Assignment
     *          ->
     *
     * @throws Exception
     */
    private void ForStat() throws Exception {
        if (nextToken().type == LexerTokenType.IDENTIFIER) {
            Assignment();
        } else {
            buildSubtreeFromStack("<null>", 0);
        }
    }

    /**
     * Expression   -> Term
     *              -> Term '<=' Term
     *              -> Term '<' Term
     *              -> Term '>=' Term
     *              -> Term '>' Term
     *              -> Term '=' Term
     *              -> Term '<>' Term
     *             
     * @throws Exception
     */
    private void Expression() throws Exception {
        Term();

        LexerToken next = nextToken();
        switch (nextToken().type) {
            case LTE:
                read(LexerTokenType.LTE);
                Term();
                buildSubtreeFromStack("<=", 2);
                break;

            case LT:
                read(LexerTokenType.LT);
                Term();
                buildSubtreeFromStack("<", 2);
                break;

            case GTE:
                read(LexerTokenType.GTE);
                Term();
                buildSubtreeFromStack(">=", 2);
                break;

            case GT:
                read(LexerTokenType.GT);
                Term();
                buildSubtreeFromStack("<", 2);
                break;

            case EQ:
                read(LexerTokenType.EQ);
                Term();
                buildSubtreeFromStack("=", 2);
                break;

            case NE:
                read(LexerTokenType.NE);
                Term();
                buildSubtreeFromStack("<>", 2);
                break;

            default:
                break;
        }
    }

    /**
     * Term -> Factor
     *      -> Term '+' Factor
     *      -> Term '-' Factor
     *      -> Term 'or' Factor
     *
     * @throws Exception
     */
    private void Term() throws Exception {
        Factor();

        LexerToken next = nextToken();
        while (next.type == LexerTokenType.PLUS || next.type == LexerTokenType.MINUS || next.type == LexerTokenType.OR) {
            switch(next.type){
                case PLUS:
                    read(LexerTokenType.PLUS);
                    Factor();
                    buildSubtreeFromStack("+", 2);
                    break;

                case MINUS:
                    read(LexerTokenType.MINUS);
                    Factor();
                    buildSubtreeFromStack("-", 2);
                    break;

                case OR:
                    read(LexerTokenType.OR);
                    Factor();
                    buildSubtreeFromStack("or", 2);
                    break;
            }
            next = nextToken();
        }
    }

    /**
     * Factor   -> Factor '*' Primary
     *          -> Factor '/' Primary
     *          -> Factor 'and' Primary
     *          -> Factor 'mod' Primary
     *          -> Primary;
     *
     * @throws Exception
     */
    private void Factor() throws Exception {
        Primary();

        LexerToken next = nextToken();
        while (next.type == LexerTokenType.MUL || next.type == LexerTokenType.DIV || next.type == LexerTokenType.AND || next.type == LexerTokenType.MOD) {
            switch(next.type){
                case MUL:
                    read(LexerTokenType.MUL);
                    Factor();
                    buildSubtreeFromStack("*", 2);
                    break;

                case DIV:
                    read(LexerTokenType.DIV);
                    Factor();
                    buildSubtreeFromStack("/", 2);
                    break;

                case AND:
                    read(LexerTokenType.AND);
                    Factor();
                    buildSubtreeFromStack("and", 2);
                    break;

                case MOD:
                    read(LexerTokenType.MOD);
                    Factor();
                    buildSubtreeFromStack("mod", 2);
                    break;
            }
            next = nextToken();
        }
    }

    /**
     * Primary  -> '-' Primary
     *          -> '+' Primary
     *          -> 'not' Primary
     *          -> 'eof'
     *          -> Name
     *          -> '<integer>'
     *          -> '<char>'
     *          -> Name '(' Expression list ',' ')'
     *          -> '(' Expression ')'
     *          -> 'succ' '(' Expression ')'
     *          -> 'pred' '(' Expression ')'
     *          -> 'chr' '(' Expression ')'
     *
     * @throws Exception
     */
    private void Primary() throws Exception {
        LexerToken next = nextToken();
        switch(nextToken().type) {
            case MINUS:
                read(LexerTokenType.MINUS);
                Primary();
                buildSubtreeFromStack("-", 1);
                break;

            case PLUS:
                read(LexerTokenType.PLUS);
                Primary();
                buildSubtreeFromStack("+", 1);
                break;

            case NOT:
                read(LexerTokenType.NOT);
                Primary();
                buildSubtreeFromStack("not", 1);
                break;

            case EOF:
                read(LexerTokenType.EOF);
                buildSubtreeFromStack("eof", 0);
                break;

            case INTEGER:
                read(next);
                break;

            case CHAR:
                read(next);
                break;

            case LEFT_PARA:
                read(LexerTokenType.LEFT_PARA);
                Expression();
                read(LexerTokenType.RIGHT_PARA);
                break;

            case SUCC:
                read(LexerTokenType.SUCC);
                read(LexerTokenType.LEFT_PARA);
                Expression();
                read(LexerTokenType.RIGHT_PARA);

                buildSubtreeFromStack("succ", 1);
                break;

            case PRED:
                read(LexerTokenType.PRED);
                read(LexerTokenType.LEFT_PARA);
                Expression();
                read(LexerTokenType.RIGHT_PARA);

                buildSubtreeFromStack("pred", 1);
                break;

            case CHR:
                read(LexerTokenType.CHR);
                read(LexerTokenType.LEFT_PARA);
                Expression();
                read(LexerTokenType.RIGHT_PARA);

                buildSubtreeFromStack("chr", 1);
                break;

            case ORD:
                read(LexerTokenType.ORD);
                read(LexerTokenType.LEFT_PARA);
                Expression();
                read(LexerTokenType.RIGHT_PARA);

                buildSubtreeFromStack("ord", 1);
                break;

            case IDENTIFIER:
                Name();
                if (nextToken().type == LexerTokenType.LEFT_PARA) {
                    read(LexerTokenType.LEFT_PARA);
                    Expression();

                    int count = 2;
                    while (nextToken().type == LexerTokenType.COMMA) {
                        read(LexerTokenType.COMMA);
                        Expression();
                        count++;
                    }
                    read(LexerTokenType.RIGHT_PARA);

                    buildSubtreeFromStack("call", count);
                }
                break;
        }
    }

    /**
     * OutExp   -> Expression
     *          -> StringNode
     *
     * @throws Exception
     */
    private void OutExp() throws Exception {
        if (nextToken().type == LexerTokenType.STRING) {
            StringNode();
        } else {
            Expression();
            buildSubtreeFromStack("integer", 1);
        }
    }

    /**
     * StringNode -> '<string>';
     * @throws Exception
     */
    private void StringNode() throws Exception {
        read(nextToken());
    }

    /**
     * Assignment   -> Name ':=' Expression
     *              -> Name ':=:' Name
     *
     * @throws Exception
     */
    private void Assignment() throws Exception {
        Name();
        LexerToken next = nextToken();
        switch (nextToken().type) {
            case ASSIGN:
                read(LexerTokenType.ASSIGN);
                Expression();
                buildSubtreeFromStack("assign", 2);
                break;

            case SWAP:
                read(LexerTokenType.SWAP);
                Name();
                buildSubtreeFromStack("swap", 2);
                break;
            default:
                throw new Exception("Invalid token");
        }
    }

    /**
     * SubProgs -> Fcn*
     *
     * @throws Exception
     */
    private void SubProgs() throws Exception {
        int count = 0;
        while (nextToken().type == LexerTokenType.FUNCTION) {
            Fcn();
            count++;
        }

        buildSubtreeFromStack("subprogs", count);
    }

    /**
     * Fcn -> 'function' Name '(' Params ')' ':' Name ';' Consts Types Dclns Body Name ';'
     *
     * @throws Exception
     */
    private void Fcn() throws Exception {
        read(LexerTokenType.FUNCTION);
        Name();
        read(LexerTokenType.LEFT_PARA);
        Params();
        read(LexerTokenType.RIGHT_PARA);
        read(LexerTokenType.COLON);
        Name();
        read(LexerTokenType.SEMICOLON);
        Consts();
        Types();
        Dclns();
        Body();
        Name();
        read(LexerTokenType.SEMICOLON);

        buildSubtreeFromStack("fcn", 8);
    }

    /**
     * Dclns -> 'var' (Dcln ';')+
     *
     * @throws Exception
     */
    private void Params() throws Exception {
        Dcln();

        int count = 1;
        while (nextToken().type == LexerTokenType.SEMICOLON) {
            read(LexerTokenType.SEMICOLON);
            Dcln();
            count++;
        }

        buildSubtreeFromStack("params", count);
    }

    /**
     * Dclns    -> 'var' (Dcln ';')+
     *          ->
     *
     * @throws Exception
     */
    private void Dclns() throws Exception {
        if (nextToken().type == LexerTokenType.VAR) {
            read(LexerTokenType.VAR);

            int count = 0;
            do {
                Dcln();
                read(LexerTokenType.SEMICOLON);
                count++;
            } while (nextToken().type == LexerTokenType.IDENTIFIER);

            buildSubtreeFromStack("dclns", count);
        } else {
            buildSubtreeFromStack("dclns", 0);
        }
    }

    /**
     * Dcln -> Name list ',' ':' Name
     *
     * @throws Exception
     */
    private void Dcln() throws Exception {
        Name();

        int count = 1;
        while (nextToken().type == LexerTokenType.COMMA) {
            read(LexerTokenType.COMMA);
            Name();
            count++;
        }
        read(LexerTokenType.COLON);
        Name();

        buildSubtreeFromStack("dcln", count);
    }

    /**
     * Types    -> 'type' (Type ';')+
     *          ->
     *
     * @throws Exception
     */
    private void Types() throws Exception {
        if (nextToken().type == LexerTokenType.TYPE) {
            read(LexerTokenType.TYPE);

            int count = 0;
            do {
                Type();
                read(LexerTokenType.SEMICOLON);
                count++;
            } while (nextToken().type == LexerTokenType.IDENTIFIER);

            buildSubtreeFromStack("types", count);
        } else {
            buildSubtreeFromStack("types", 0);
        }
    }

    /**
     * Type -> Name '=' LitList
     *
     * @throws Exception
     */
    private void Type() throws Exception {
        Name();
        read(LexerTokenType.EQ);
        LitList();

        buildSubtreeFromStack("type", 2);
    }

    /**
     * LitList -> '(' Name list ',' ')'
     * @throws Exception
     */
    private void LitList() throws Exception {
        read(LexerTokenType.LEFT_PARA);
        Name();

        int count = 1;
        while (nextToken().type == LexerTokenType.COMMA) {
            read(LexerTokenType.COMMA);
            Name();
            count++;
        }
        read(LexerTokenType.RIGHT_PARA);

        buildSubtreeFromStack("lit", count);
    }

    /**
     * Consts   -> 'const' Const list ',' ';'
     *          ->
     * @throws Exception
     */
    private void Consts() throws Exception {
        LexerToken nextToken = nextToken();
        if (nextToken.type == LexerTokenType.CONST) {
            read(LexerTokenType.CONST);
            Const();

            int count = 1;
            while (nextToken().type == LexerTokenType.COMMA) {
                read(LexerTokenType.COMMA);
                Const();
                count++;
            }
            read(LexerTokenType.SEMICOLON);

            buildSubtreeFromStack("consts", count);
        } else {
            buildSubtreeFromStack("consts", 0);
        }
    }

    /**
     * Const -> Name '=' ConstValue
     *
     * @throws Exception
     */
    private void Const() throws Exception {
        Name();
        read(LexerTokenType.EQ);
        ConstValue();

        buildSubtreeFromStack("const", 2);
    }

    /**
     * ConstValue   -> '<integer>'
     * -> '<char>'
     * -> Name;
     *
     * @throws Exception
     */
    private void ConstValue() throws Exception {
        LexerToken next = nextToken();
        switch (nextToken().type) {
            case INTEGER:
            case CHAR:
                read(next);
                break;

            case IDENTIFIER:
                Name();
        }
    }

    /**
     * Name -> '<identifier>'
     *
     * @throws Exception
     */
    private void Name() throws Exception {
        read(nextToken());
    }

    /**
     * Build Bottom up subtree from stack
     *
     * @param name AST node name
     * @param childrenCount no of children for AST node. In other words how many subtrees to pop from the stack
     */
    private void buildSubtreeFromStack(String name, int childrenCount) {
        ASTNode parent = new ASTNode(name);
        for (int i = 0; i < childrenCount; i++) {
            parent.addChild(stack.pop());
        }

        stack.push(parent);
    }

    /**
     * Used for reading/verifying keyword tokens
     *
     * @param tokenType keyword token
     * @throws Exception
     */
    private void read(LexerTokenType tokenType) throws Exception {
        verifyToken(tokenType);
    }

    /**
     * Used to verify and move forward from the variable and literal related tokens
     * Token type -> token value
     * Inserts node into stack
     *
     * @param token variable or literal token
     * @throws Exception
     */
    private void read(LexerToken token) throws Exception {
        verifyToken(token.type);

        ASTNode typeNode = new ASTNode(token.type.val);
        ASTNode valueNode = new ASTNode(token);
        typeNode.addChild(valueNode);

        stack.push(typeNode);
    }

    /**
     * Check if token given is the one expected in the grammar
     *
     * @param expected
     * @throws Exception
     */
    private void verifyToken(LexerTokenType expected) throws Exception {
        LexerTokenType nextToken = nextToken().type;
        System.out.println(nextToken);
        if (nextToken == expected) {
            pos++;
        } else {
            throw new Exception("Unexpected token " + nextToken + " Expected: " + expected);
        }
    }

    private LexerToken nextToken() {
        if (pos < tokenSequence.size()) {
            return tokenSequence.get(pos);
        } else {
            System.out.println("End of tokens");
            return null;
        }
    }
}
