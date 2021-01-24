import java.util.ArrayList;
import java.util.Stack;

public class ASTNode {
    public String value;
    public ArrayList<ASTNode> children = new ArrayList<ASTNode>();

    ASTNode(LexerToken token) {
        value = token.value;
    }
    ASTNode(String val) {
        value = val;
    }

    public void addChild(ASTNode n) {
        children.add(n);
    }

    public ASTNode getChild(int n) {
        return children.get(n);
    }

    public String traverse(int d) {
        String treeStr = "";
        for (int i = 0; i < d; i++) {
            System.out.print(". ");
            treeStr += ". ";
        }
        treeStr += this.value;
        System.out.print(this.value);
        treeStr += "(" + children.size() + ")"  + "\n";
        System.out.println("(" + children.size() + ")");
        for (int j=0; j< children.size(); j++) {
            getChild(j).traverse(d+1);
        }

        return treeStr;
    }


}
