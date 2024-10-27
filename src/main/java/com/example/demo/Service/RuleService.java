package com.example.demo.Service;
import java.util.List;
import java.util.Map;

import com.example.demo.Model.Node;
import com.example.demo.Model.Rule;

public interface RuleService {
    Rule createRule(Rule rule);
    Rule getRuleById(Long id);
    List<Rule> getAllRules();
    void updateRule(Long id, Rule ruleUpdates);
    void deleteRule(Long id);
    
    Node createRuleFromString(String ruleString); // For creating AST from rule string
    Node combineRules(List<String> rules); // For combining multiple rules into a single AST
    boolean evaluateRule(Node ast, Map<String, Object> data); // For evaluating the AST against user data
   
    
}