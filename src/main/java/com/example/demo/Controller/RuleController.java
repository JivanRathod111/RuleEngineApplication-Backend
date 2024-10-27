package com.example.demo.Controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import org.hibernate.validator.internal.util.logging.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Exception.RuleNotFoundException;
import com.example.demo.Model.Node;
import com.example.demo.Model.Rule;
import com.example.demo.Model.RuleResponse;
import com.example.demo.Repository.RuleRepository;
//import com.example.demo.Model.EvaluateRequest; // Import the new class
import com.example.demo.Service.RuleService;
import com.fasterxml.jackson.databind.ObjectMapper;

//import ch.qos.logback.classic.Logger;

@RestController
@RequestMapping("/api/rules")
public class RuleController {

    @Autowired
    private RuleService ruleService;
    
    @Autowired
    RuleRepository ruleRepository;

    @PostMapping
    public ResponseEntity<Rule> createRule(@RequestBody Rule rule) {
        Rule createdRule = ruleService.createRule(rule);
        return new ResponseEntity<>(createdRule, HttpStatus.CREATED);
    }

//    @GetMapping("/{id}")
//    public ResponseEntity<Rule> getRuleById(@PathVariable Long id) {
//        Rule rule = ruleService.getRuleById(id);
//        if (rule == null) {
//            throw new RuleNotFoundException("Rule not found with id: " + id);
//        }
//        return new ResponseEntity<>(rule, HttpStatus.OK);
//    }

    @GetMapping
    public ResponseEntity<List<Rule>> getAllRules() {
        List<Rule> rules = ruleService.getAllRules();
        return new ResponseEntity<>(rules, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateRule(@PathVariable Long id, @RequestBody Rule rule) {
        ruleService.updateRule(id, rule);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRule(@PathVariable Long id) {
        ruleService.deleteRule(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

//    @PostMapping("/create")
//   public ResponseEntity<Node> createRuleFromString(@RequestBody Map<String, String> requestBody) {
////        String ruleString = requestBody.get("ruleString");
////        Node ast = ruleService.createRuleFromString(ruleString);
////        return ResponseEntity.ok(ast);
////    }
//    
    @PostMapping("/create")
    public ResponseEntity<RuleResponse> createRuleFromString(@RequestBody Map<String, String> requestBody) {
        String ruleString = requestBody.get("ruleString");

        // Save the Rule to the database
        Rule rule = new Rule(ruleString);
        rule = ruleRepository.save(rule);  // Ensure ruleRepository is defined and injected

        // Generate the AST
        Node ast = ruleService.createRuleFromString(ruleString);

        // Create RuleResponse with id, ruleString, and ast
        RuleResponse response = new RuleResponse(rule.getId(), rule.getRuleString(), ast);

        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<RuleResponse> getRuleById(@PathVariable Long id) {
        // Find the rule by ID in the database
        Optional<Rule> ruleOptional = ruleRepository.findById(id);
        
        // If the rule is not found, return a 404 response
        if (ruleOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Rule rule = ruleOptional.get();

        // Generate the AST based on ruleString
        Node ast = ruleService.createRuleFromString(rule.getRuleString());

        // Create RuleResponse with id, ruleString, and ast
        RuleResponse response = new RuleResponse(rule.getId(), rule.getRuleString(), ast);

        return ResponseEntity.ok(response);
    }



    @PostMapping("/combine")
    public ResponseEntity<Node> combineRules(@RequestBody CombineRequest request) {
        List<String> rules = request.getRules();
        if (rules == null || rules.isEmpty()) {
            return ResponseEntity.badRequest().body(null); // Return a bad request if no rules provided
        }
        
        // Combine the rules and create the AST
        Node combinedAST = ruleService.combineRules(rules);
        
        // Return the response with the combined AST
        return ResponseEntity.ok(combinedAST); // Return the AST directly
    }

    
    
    
    
    

    @PostMapping("/evaluate")
    public ResponseEntity<Map<String, Object>> evaluateRule(@RequestBody EvaluationRequest request) {
        Node ast = request.getAst();
        List<Map<String, Object>> usersData = request.getData();

        if (ast == null || usersData == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.singletonMap("error", "Missing AST or user data"));
        }

        List<Boolean> results = new ArrayList<>();

        // Evaluate rule for each user
        for (Map<String, Object> user : usersData) {
            try {
                boolean result = ruleService.evaluateRule(ast, user);
                results.add(result);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.singletonMap("error", e.getMessage()));
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("error", "Error evaluating the rule"));
            }
        }

        return ResponseEntity.ok(Collections.singletonMap("results", results));
    }
    
   
}   


   