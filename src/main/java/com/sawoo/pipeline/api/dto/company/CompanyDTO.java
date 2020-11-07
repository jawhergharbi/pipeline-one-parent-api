package com.sawoo.pipeline.api.dto.company;

import com.googlecode.jmapper.annotations.JMap;
import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyDTO {

    @JMap
    private String id;

    @JMap
    @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR)
    private String name;

    @JMap
    @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR)
    private String url;

    @JMap
    private Integer headcount;

    @JMap
    private LocalDateTime created;

    @JMap
    private LocalDateTime updated;
}
