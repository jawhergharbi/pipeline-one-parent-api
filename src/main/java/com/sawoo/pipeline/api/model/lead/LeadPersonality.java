package com.sawoo.pipeline.api.model.lead;

import com.googlecode.jmapper.annotations.JMap;
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
public class LeadPersonality {

    @JMap
    private String description;

    @JMap
    private String url;

    @JMap
    private LocalDateTime updated;

    @JMap
    private Integer type;
}
