package com.sawoo.pipeline.api.model.interaction;

import lombok.Data;

@Data
public class InteractionSource {

    private InteractionSourceType type;
    private String sourceId;

}
