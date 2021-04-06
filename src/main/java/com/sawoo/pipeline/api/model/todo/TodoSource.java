package com.sawoo.pipeline.api.model.todo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class TodoSource {

    private TodoSourceType type;
    private String sourceId;
    private String sourceDescription;

}
