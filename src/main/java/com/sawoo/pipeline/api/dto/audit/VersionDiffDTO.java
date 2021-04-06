package com.sawoo.pipeline.api.dto.audit;

import lombok.Data;

@Data
public class VersionDiffDTO {

    private String propertyName;
    private Object left;
    private Object right;
    private String propertyNameWithPath;

}
