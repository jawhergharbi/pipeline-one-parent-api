package com.sawoo.pipeline.api.dto.sequence;

import com.googlecode.jmapper.annotations.JMap;
import com.sawoo.pipeline.api.model.BaseEntity;
import com.sawoo.pipeline.api.model.sequence.SequenceStepChannel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder(toBuilder = true)
public class SequenceStepDTO extends BaseEntity {

    @JMap
    @Id
    private String id;

    @JMap
    private String historyId;

    @JMap
    private Integer position;

    @JMap
    private Integer timespan;

    @JMap
    private Integer personality;

    @JMap
    private String message;

    @JMap
    private String attachment;

    @JMap
    private String version;

    @JMap
    private SequenceStepChannel channel;


}
