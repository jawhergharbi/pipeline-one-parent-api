package com.sawoo.pipeline.api.dto.sequence;

import com.googlecode.jmapper.annotations.JMap;
import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.dto.BaseEntityDTO;
import com.sawoo.pipeline.api.dto.account.AccountFieldDTO;
import com.sawoo.pipeline.api.model.sequence.SequenceStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder(toBuilder = true)
public class SequenceDTO extends BaseEntityDTO {

    @JMap
    private String id;

    @JMap
    @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR)
    private String name;

    @JMap
    private String description;

    @JMap
    private SequenceStatus status;

    @JMap
    @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR)
    private String componentId;

    @JMap
    @Valid
    @NotEmpty(message = ExceptionMessageConstants.COMMON_LIST_FIELD_CAN_NOT_BE_EMPTY_ERROR)
    private Set<SequenceUserDTO> users;

    private String ownerId;

    private AccountFieldDTO account;
}
