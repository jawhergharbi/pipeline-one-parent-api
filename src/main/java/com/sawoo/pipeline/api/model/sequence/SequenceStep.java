package com.sawoo.pipeline.api.model.sequence;

import com.googlecode.jmapper.annotations.JMap;
import com.sawoo.pipeline.api.model.BaseEntity;
import com.sawoo.pipeline.api.model.DBConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Document(collection = DBConstants.SEQUENCE_STEP_DOCUMENT)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
public class SequenceStep extends BaseEntity {

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
