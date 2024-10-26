package com.example.demo.Model;

public class RuleResponse {

    private Long id;
    private String ruleString;
    private Node ast;

    public RuleResponse(Long id, String ruleString, Node ast) {
        this.id = id;
        this.ruleString = ruleString;
        this.ast = ast;
    }

    public Long getId() {
        return id;
    }

    public String getRuleString() {
        return ruleString;
    }

    public Node getAst() {
        return ast;
    }
}
