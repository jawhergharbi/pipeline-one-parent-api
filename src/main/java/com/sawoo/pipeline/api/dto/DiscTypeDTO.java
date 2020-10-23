package com.sawoo.pipeline.api.dto;

import com.googlecode.jmapper.annotations.JMap;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiscTypeDTO {

    @JMap
    private Integer type;
    private String color;
    private String[] tips;
 }
