package com.sawoo.pipeline.api.model.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class MessageTemplate {

    private String text;
    private Map<String, MessageTemplateVariable> variables;
    private List<Link> links;

    public void addVariable(String key, String value) {
        MessageTemplateVariable variable = MessageTemplateVariable.builder()
                .key(key)
                .value(value)
                .build();
        getVariables().put(key, variable);
    }

    public MessageTemplateVariable removeVariable(String key) {
        return getVariables().remove(key);
    }


    public Map<String, MessageTemplateVariable> getVariables() {
        if (variables == null) {
            variables = new HashMap<>();
        }
        return variables;
    }
}
