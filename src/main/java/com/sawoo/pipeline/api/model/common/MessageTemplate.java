package com.sawoo.pipeline.api.model.common;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class MessageTemplate {

    private String text;
    private Map<String, MessageTemplateVariable> variables;
    private List<Link> links;
}
