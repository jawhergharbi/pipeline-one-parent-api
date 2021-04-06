package com.sawoo.pipeline.api.model.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
}
