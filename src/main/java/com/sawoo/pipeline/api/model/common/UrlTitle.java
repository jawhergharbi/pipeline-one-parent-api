package com.sawoo.pipeline.api.model.common;

import com.googlecode.jmapper.annotations.JMap;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UrlTitle {

    @JMap
    private String description;

    @JMap
    private String url;
}
