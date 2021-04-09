package com.sawoo.pipeline.api.dto.todo;

import com.googlecode.jmapper.annotations.JMap;
import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class TodoSearchDTO {

    @JMap
    @Valid
    @NotEmpty(message = ExceptionMessageConstants.COMMON_LIST_FIELD_CAN_NOT_BE_EMPTY_ERROR)
    private List<String> componentIds;

    @JMap
    private List<Integer> status;

    @JMap
    private List<Integer> types;

    @JMap
    private List<String> sourceId;

    @JMap
    private List<Integer> sourceType;

}
