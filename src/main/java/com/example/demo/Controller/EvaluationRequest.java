package com.example.demo.Controller;

import java.util.Map;

import com.example.demo.Model.Node;



import java.util.List;
import java.util.Map;

import com.example.demo.Model.Node;

public class EvaluationRequest {
    private Node ast;
    private List<Map<String, Object>> data;

    public Node getAst() {
        return ast;
    }

    public void setAst(Node ast) {
        this.ast = ast;
    }

    public List<Map<String, Object>> getData() {
        return data;
    }

    public void setData(List<Map<String, Object>> data) {
        this.data = data;
    }
}