package com.sawoo.pipeline.api.model.common;

import com.googlecode.jmapper.annotations.JMap;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cloud.gcp.data.datastore.core.mapping.Entity;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class UrlTitle {

    @JMap
    private String title;

    @JMap
    private String url;
}
