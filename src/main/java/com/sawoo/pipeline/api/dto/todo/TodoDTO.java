package com.sawoo.pipeline.api.dto.todo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.googlecode.jmapper.annotations.JMap;
import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.validation.EnumIntValue;
import com.sawoo.pipeline.api.dto.BaseEntityDTO;
import com.sawoo.pipeline.api.dto.common.LinkDTO;
import com.sawoo.pipeline.api.model.todo.TodoSource;
import com.sawoo.pipeline.api.model.todo.TodoStatus;
import com.sawoo.pipeline.api.model.todo.TodoType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.With;
import lombok.experimental.SuperBuilder;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@SuperBuilder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TodoDTO extends BaseEntityDTO {

    @JMap
    @With
    private String id;

    @JMap
    @NotNull(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_NULL_ERROR)
    private Integer channel;

    @JMap
    @EnumIntValue(enumCLass = TodoStatus.class)
    private Integer status;

    @JMap
    @Valid
    private LinkDTO link;

    @JMap
    private TodoMessageDTO message;

    @JMap
    @NotNull(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_NULL_ERROR)
    private LocalDateTime scheduled;

    @JMap
    private LocalDateTime completionDate;

    @JMap
    private String componentId;

    @JMap
    private String campaignId;

    @JMap
    @NotNull(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_NULL_ERROR)
    private String assigneeId;

    @JMap
    private TodoSource source;

    @JMap
    private TodoType type;
}
