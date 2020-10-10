package com.sawoo.pipeline.api.dto.lead;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class LeadInteractionRequestDTO extends LeadInteractionBaseDTO {

    @NotNull(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_NULL_ERROR)
    private LocalDateTime dateTime;
}
