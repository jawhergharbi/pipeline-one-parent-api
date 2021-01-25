package com.sawoo.pipeline.api.dto.interaction;

import com.googlecode.jmapper.annotations.JMap;
import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.dto.BaseEntityDTO;
import com.sawoo.pipeline.api.model.common.Note;
import com.sawoo.pipeline.api.model.common.UrlTitle;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder(toBuilder = true)
public class InteractionDTO extends BaseEntityDTO {

    @JMap
    private String id;

    @JMap
    @NotNull(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_NULL_ERROR)
    private Integer type;

    @JMap
    @NotNull(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_NULL_ERROR)
    private Integer status;

    @JMap
    private UrlTitle link;

    @JMap
    private Note note;

    @JMap
    @NotNull(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_NULL_ERROR)
    private LocalDateTime scheduled;

    @JMap
    private String componentId;
}
