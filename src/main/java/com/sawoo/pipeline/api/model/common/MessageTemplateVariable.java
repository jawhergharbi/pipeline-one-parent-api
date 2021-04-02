package com.sawoo.pipeline.api.model.common;

import lombok.Data;

@Data
public class MessageTemplateVariable {

    private String key;
    private String value;
    private MessageTemplateVariableType type;
}
