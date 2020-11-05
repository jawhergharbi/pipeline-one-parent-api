package com.sawoo.pipeline.api.model.common;

import com.googlecode.jmapper.annotations.JMap;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cloud.gcp.data.datastore.core.mapping.Unindexed;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NoteMongoDB {

    @JMap
    @Unindexed
    private String text;

    @JMap
    private LocalDateTime updated;
}
