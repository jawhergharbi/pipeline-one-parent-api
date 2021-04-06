package com.sawoo.pipeline.api.dto.sequence;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.googlecode.jmapper.annotations.JMap;
import com.googlecode.jmapper.annotations.JMapConversion;
import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.dto.account.AccountFieldDTO;
import com.sawoo.pipeline.api.model.sequence.SequenceStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@SuperBuilder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SequenceDTO extends SequenceBaseDTO {

    @JMap
    private Integer status;

    @JMap
    @Valid
    @NotEmpty(message = ExceptionMessageConstants.COMMON_LIST_FIELD_CAN_NOT_BE_EMPTY_ERROR)
    private Set<SequenceUserDTO> users;

    private String ownerId;

    private AccountFieldDTO account;

    @JMapConversion(from = {"status"}, to = {"status"})
    public Integer statusConversion(SequenceStatus status) {
        return status != null ? status.getValue() : -1;
    }

}
