package com.sawoo.pipeline.api.model;

import com.googlecode.jmapper.annotations.JMap;
import com.sawoo.pipeline.api.model.common.Note;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cloud.gcp.data.datastore.core.mapping.Entity;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Status {

    @JMap
    private int value;

    @JMap
    private Note notes;

    @JMap
    private LocalDateTime updated;
}
