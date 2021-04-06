package com.sawoo.pipeline.api.model.todo;

import com.sawoo.pipeline.api.model.common.MessageTemplate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class TodoMessage {

    private String text;
    private MessageTemplate template;
}
