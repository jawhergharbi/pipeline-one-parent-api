package com.sawoo.pipeline.api.model.todo;

import com.googlecode.jmapper.annotations.JMap;
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

    @JMap
    private String text;

    @JMap
    private MessageTemplate template;

    @JMap
    private boolean valid;
}
