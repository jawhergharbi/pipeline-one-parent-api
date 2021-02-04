package com.sawoo.pipeline.api.dto.interaction;

import com.sawoo.pipeline.api.dto.UserCommon;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.With;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@SuperBuilder
public class InteractionAssigneeDTO extends InteractionDTO {

    @With
    UserCommon assignee;
}
