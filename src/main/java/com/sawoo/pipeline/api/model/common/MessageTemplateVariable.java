package com.sawoo.pipeline.api.model.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class MessageTemplateVariable {

    private String key;
    private String value;
    private MessageTemplateVariableType type;
}
