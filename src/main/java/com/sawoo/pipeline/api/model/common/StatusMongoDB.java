package com.sawoo.pipeline.api.model.common;

import com.googlecode.jmapper.annotations.JMap;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StatusMongoDB {

    @JMap
    private int value;

    @JMap
    private Note notes;

    @JMap
    private LocalDateTime updated;
}
