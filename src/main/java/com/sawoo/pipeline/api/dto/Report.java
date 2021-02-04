package com.sawoo.pipeline.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Report<T> {

    private String template;
    private T templateData;
    private Integer type;
    private String locale;
}
