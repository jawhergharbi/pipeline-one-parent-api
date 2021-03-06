package com.sawoo.pipeline.api.dto.todo;

import com.googlecode.jmapper.annotations.JMap;
import com.sawoo.pipeline.api.dto.common.MessageTemplateDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class TodoMessageDTO {

    @JMap
    private String text;

    @JMap
    private MessageTemplateDTO template;

    @JMap
    private boolean valid;
}
