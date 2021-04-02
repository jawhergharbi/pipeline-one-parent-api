package com.sawoo.pipeline.api.model.todo;

import com.sawoo.pipeline.api.model.common.MessageTemplate;
import lombok.Data;

@Data
public class TodoMessage {

    private String text;
    private MessageTemplate template;
}
