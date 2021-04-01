package com.sawoo.pipeline.api.model.todo;

import lombok.Data;

@Data
public class TodoSource {

    private TodoSourceType type;
    private String sourceId;

}
