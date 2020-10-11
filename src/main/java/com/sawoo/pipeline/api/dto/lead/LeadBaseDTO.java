package com.sawoo.pipeline.api.dto.lead;

import com.googlecode.jmapper.annotations.JMap;
import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.dto.company.CompanyDTO;
import com.sawoo.pipeline.api.model.lead.LeadPersonality;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeadBaseDTO {
    @JMap
    private Long id;

    @JMap(attributes = {"firstName", "lastName"})
    private String fullName;

    @JMap
    @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR)
    private String firstName;

    @JMap
    @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR)
    private String lastName;

    @JMap
    @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR)
    private String position;

    @JMap
    @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR)
    private String linkedInUrl;

    @JMap
    @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR)
    private String linkedInThread;

    @JMap
    @Valid
    @NotNull(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_NULL_ERROR)
    private CompanyDTO company;

    @JMap
    private LeadPersonalityDTO personality;
}
