package com.sawoo.pipeline.api.dto;

import com.googlecode.jmapper.annotations.JMap;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseEntityDTO {
    @JMap
    private LocalDateTime created;

    @JMap
    private LocalDateTime updated;
}
