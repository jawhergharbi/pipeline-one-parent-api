package com.sawoo.pipeline.api.model.todo;


import com.googlecode.jmapper.annotations.JMap;
import com.sawoo.pipeline.api.model.BaseEntity;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.common.Note;
import com.sawoo.pipeline.api.model.common.Link;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
@Document(collection = DBConstants.TODO_DOCUMENT)
public class Todo extends BaseEntity {

    @JMap
    @Id
    private String id;

    @JMap
    private Integer type;

    @JMap
    private Integer status;

    @JMap
    private Link link;

    @JMap
    private Note note;

    @JMap
    private LocalDateTime scheduled;

    @JMap
    private String componentId;

    @JMap
    private String assigneeId;

    @JMap
    private TodoSource source;
}
