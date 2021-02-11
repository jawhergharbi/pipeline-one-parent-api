package com.sawoo.pipeline.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCommon {

    private String id;

    private String fullName;

    private UserCommonType type;
}
