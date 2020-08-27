package com.sawoo.pipeline.api.model;

import com.googlecode.jmapper.annotations.JMap;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cloud.gcp.data.datastore.core.mapping.Entity;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "company")
public class Company {

    @Id
    @JMap
    private Long id;

    @JMap
    private String name;

    @JMap
    private String url;

    @JMap
    private Integer headcount;

    @JMap
    private LocalDateTime created;

    @JMap
    private LocalDateTime updated;
}
