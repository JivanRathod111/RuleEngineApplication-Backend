package com.example.demo.Service.Impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.Exception.RuleNotFoundException;
import com.example.demo.Model.Node;
import com.example.demo.Model.Rule;
import com.example.demo.Repository.RuleRepository;
import com.example.demo.Service.RuleService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

@Service
public class RuleServiceImpl implements RuleService {

    @Autowired
    private RuleRepository ruleRepository;

    @Override
    public Rule createRule(Rule rule) {
        return ruleRepository.save(rule);
    }

    @Override
    public Rule getRuleById(Long id) {
        return ruleRepository.findById(id)
                .orElseThrow(() -> new RuleNotFoundException("Rule not found with id: " + id));
    }

    @Override
    public List<Rule> getAllRules() {
        return ruleRepository.findAll();
    }
    


    @Override
    public void updateRule(Long id, Rule ruleUpdates) {
        Rule existingRule = getRuleById(id); // Will throw RuleNotFoundException if not found

        if (ruleUpdates.getRuleString() != null) {
            existingRule.setRuleString(ruleUpdates.getRuleString());
        }

        ruleRepository.save(existingRule);
    }

    @Override
    public void deleteRule(Long id) {
        Rule existingRule = getRuleById(id); // Will throw RuleNotFoundException if not found
        ruleRepository.delete(existingRule);
    }

    @Override
    public Node createRuleFromString(String ruleString) {
        return parseToAST(ruleString);
    }
    
    private int precedence(String operator) {
        switch (operator) {
            case "AND":
                return 1;
            case "OR":
                return 0;
            default:
                return -1;
        }
    }
    
    private Node parseToAST(String ruleString) {
        
        ruleString = ruleString.trim().replaceAll(" +", " ");

        
        List<String> tokens = new ArrayList<>();
        StringBuilder currentToken = new StringBuilder();
        for (char ch : ruleString.toCharArray()) {
            if (ch == ' ' || ch == '(' || ch == ')') {
                if (currentToken.length() > 0) {
                    tokens.add(currentToken.toString());
                    currentToken.setLength(0); 
                }
                if (ch == '(' || ch == ')') {
                    tokens.add(String.valueOf(ch));
                }
            } else {
                currentToken.append(ch);
            }
        }
        if (currentToken.length() > 0) {
            tokens.add(currentToken.toString());
        }

       
        Stack<Node> nodeStack = new Stack<>();
        Stack<String> operatorStack = new Stack<>();

        int index = 0; 
        
        while (index < tokens.size()) {
            String token = tokens.get(index);
            System.out.println("Processing token: " + token); 

            if (token.equals("AND") || token.equals("OR")) {
                while (!operatorStack.isEmpty() && precedence(token) <= precedence(operatorStack.peek())) {
                    String op = operatorStack.pop();
                    Node right = nodeStack.pop();  
                    Node left = nodeStack.pop();   
                    nodeStack.push(new Node("operator", left, right, op));
                }
                operatorStack.push(token);
            } else if (token.equals("(")) {
                operatorStack.push(token);
            } else if (token.equals(")")) {
                while (!operatorStack.isEmpty() && !operatorStack.peek().equals("(")) {
                    String op = operatorStack.pop();
                    Node right = nodeStack.pop();
                    Node left = nodeStack.pop();
                    nodeStack.push(new Node("operator", left, right, op));
                }
                operatorStack.pop(); 
            } else {
                
                String leftOperand = token; 
                index++; 

               
                if (index < tokens.size() && (tokens.get(index).matches("[<>!=]+"))) {
                    String operator = tokens.get(index);
                    index++; 

                   
                    if (index < tokens.size() && (tokens.get(index).matches("'[a-zA-Z]+'|\\d+"))) {
                        String value = tokens.get(index).replace("'", ""); 
                        
                        Node leftNode = new Node("operand", null, null, leftOperand);
                        Node rightNode = new Node("operand", null, null, value);
                        
                        
                        Node conditionNode = new Node("operator", leftNode, rightNode, operator);
                        
                       
                        nodeStack.push(conditionNode);
                    } else {
                        System.out.println("Expected a value token after the operator for: " + leftOperand + " " + operator);
                    }
                } else {
                    System.out.println("Expected an operator after the left operand: " + leftOperand);
                }
            }
            index++; 
        }

        while (!operatorStack.isEmpty()) {
            String op = operatorStack.pop();
            Node right = nodeStack.pop();
            Node left = nodeStack.pop();
            nodeStack.push(new Node("operator", left, right, op));
        }

        return nodeStack.isEmpty() ? null : nodeStack.pop(); 
    }
    
    
    @Override
    public Node combineRules(List<String> rules) {
        Node combinedAst = null;
        for (String rule : rules) {
            Node ast = createRuleFromString(rule); // Assume this method creates a Node from a rule string
            combinedAst = combineASTs(combinedAst, ast); // Combine the current AST with the new one
        }
        return combinedAst;
    }

    private Node combineASTs(Node existingAst, Node newAst) {
        if (existingAst == null) {
            return newAst; 
        }
        return new Node("OR", existingAst, newAst); // Combine using OR, you can change this based on your logic
    }
    
    

    @Override
    public boolean evaluateRule(Node ast, Map<String, Object> data) {
        return evaluateAST(ast, data);
    }

    private boolean evaluateAST(Node ast, Map<String, Object> data) {
        if (ast.getType().equals("operand")) {
            String[] parts = ((String) ast.getValue()).split(" ");
            String attribute = parts[0]; // e.g., "age"
            String operator = parts[1];  // e.g., ">"
            String valueStr = parts[2];  // e.g., "30"

            Object attributeValue = data.get(attribute); // e.g., data.get("age") -> 35

            if (attributeValue == null) {
                return false; // If the attribute doesn't exist in the user data
            }

            if (attributeValue instanceof Number) {
                // Handle numeric comparisons
                int attributeIntValue = ((Number) attributeValue).intValue();
                int comparisonValue = Integer.parseInt(valueStr);

                switch (operator) {
                    case ">":
                        return attributeIntValue > comparisonValue;
                    case "<":
                        return attributeIntValue < comparisonValue;
                    case "=":
                        return attributeIntValue == comparisonValue;
                    case ">=":
                        return attributeIntValue >= comparisonValue;
                    case "<=":
                        return attributeIntValue <= comparisonValue;
                    default:
                        throw new IllegalArgumentException("Invalid operator for numeric comparison: " + operator);
                }
            } else if (attributeValue instanceof String) {
                // Handle string comparisons
                String attributeStringValue = (String) attributeValue;

                // Remove single quotes from valueStr if it's a string comparison
                String comparisonValue = valueStr.replace("'", "");

                switch (operator) {
                    case "=":
                        return attributeStringValue.equals(comparisonValue);
                    case "!=":
                        return !attributeStringValue.equals(comparisonValue);
                    default:
                        throw new IllegalArgumentException("Invalid operator for string comparison: " + operator);
                }
            } else {
                throw new IllegalArgumentException("Unsupported attribute type for comparison");
            }
        } else if (ast.getType().equals("operator")) {
            boolean leftResult = evaluateAST(ast.getLeft(), data);
            boolean rightResult = evaluateAST(ast.getRight(), data);

            switch ((String) ast.getValue()) {
                case "AND":
                    return leftResult && rightResult;
                case "OR":
                    return leftResult || rightResult;
                default:
                    throw new IllegalArgumentException("Invalid operator: " + ast.getValue());
            }
        }
        throw new IllegalArgumentException("Invalid AST node type: " + ast.getType());
    }


    
    
}