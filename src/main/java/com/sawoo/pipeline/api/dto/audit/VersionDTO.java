package com.sawoo.pipeline.api.dto.audit;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class VersionDTO<D> {

    private Integer version;
    private boolean isCurrentVersion;
    private String author;
    private LocalDateTime created;
    private D entity;

}
