package com.sawoo.pipeline.api.model.sequence;

import com.sawoo.pipeline.api.model.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
public class SequenceUser extends BaseEntity {

    private String userId;

    private SequenceUserType type;
}
