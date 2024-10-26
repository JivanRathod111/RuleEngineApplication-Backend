package com.example.demo.Controller;

import com.example.demo.Model.RuleCondition;
import com.example.demo.Service.ConditionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/conditions")
public class ConditionController {

    @Autowired
    private ConditionService ruleConditionService;

    @GetMapping("/{id}")
    public ResponseEntity<RuleCondition> getConditionById(@PathVariable Long id) {
        RuleCondition condition = ruleConditionService.getConditionById(id);
        return new ResponseEntity<>(condition, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<RuleCondition>> getAllConditions() {
        List<RuleCondition> conditions = ruleConditionService.getAllConditions();
        return new ResponseEntity<>(conditions, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<RuleCondition> createCondition(@RequestBody RuleCondition condition) {
        RuleCondition createdCondition = ruleConditionService.createCondition(condition);
        return new ResponseEntity<>(createdCondition, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateCondition(@PathVariable Long id, @RequestBody RuleCondition condition) {
        ruleConditionService.updateCondition(id, condition);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCondition(@PathVariable Long id) {
        ruleConditionService.deleteCondition(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
