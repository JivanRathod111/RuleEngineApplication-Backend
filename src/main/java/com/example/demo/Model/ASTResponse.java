package com.example.demo.Model;

public class ASTResponse {
    private Node combinedAST;

    public ASTResponse(Node combinedAST) {
        this.combinedAST = combinedAST;
    }

    public Node getCombinedAST() {
        return combinedAST;
    }

    public void setCombinedAST(Node combinedAST) {
        this.combinedAST = combinedAST;
    }
}
