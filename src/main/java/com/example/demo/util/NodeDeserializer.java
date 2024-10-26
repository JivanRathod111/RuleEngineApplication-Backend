package com.example.demo.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;
import com.example.demo.Model.Node;

import java.io.IOException;

@Component
public class NodeDeserializer extends JsonDeserializer<Node> {

    @Override
    public Node deserialize(JsonParser p, DeserializationContext ctxt) 
        throws IOException, JsonProcessingException {
        JsonNode node = p.getCodec().readTree(p);

        return parseNode(node);
    }

    private Node parseNode(JsonNode jsonNode) {
        Node node = new Node();
        node.setType(jsonNode.get("type").asText());
        node.setValue(jsonNode.get("value").asText());

        if (jsonNode.has("left")) {
            node.setLeft(parseNode(jsonNode.get("left")));
        }

        if (jsonNode.has("right")) {
            node.setRight(parseNode(jsonNode.get("right")));
        }

        return node;
    }
}
