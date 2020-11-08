package com.sawoo.pipeline.api.model;

import com.googlecode.jmapper.annotations.JMap;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public abstract class EntityBase {

    @JMap
    private LocalDateTime created;

    @JMap
    private LocalDateTime updated;
}
