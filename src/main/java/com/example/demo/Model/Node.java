package com.example.demo.Model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.example.demo.util.NodeDeserializer;

@JsonDeserialize(using = NodeDeserializer.class)
public class Node {
    private String type;
    private Node left;
    private Node right;
    private String value;

    public Node() {
        super();
    }

    public Node(String type, Node left, Node right) {
        this.type = type;
        this.left = left;
        this.right = right;
        this.value = null;  
    }

    public Node(String type, Node left, Node right, String value) {
        this.type = type;
        this.left = left;
        this.right = right;
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Node getLeft() {
        return left;
    }

    public void setLeft(Node left) {
        this.left = left;
    }

    public Node getRight() {
        return right;
    }

    public void setRight(Node right) {
        this.right = right;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return toJson();
    }

    // Convert the node and its children to a JSON-like structure
    public String toJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("{")
          .append("\"type\":\"").append(type).append("\",")
          .append("\"value\":\"").append(value).append("\",")
          .append("\"left\":").append(left != null ? left.toJson() : "null").append(",")
          .append("\"right\":").append(right != null ? right.toJson() : "null")
          .append("}");
        return sb.toString();
    }
}
