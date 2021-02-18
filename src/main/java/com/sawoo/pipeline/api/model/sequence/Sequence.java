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
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Document(collection = DBConstants.SEQUENCE_DOCUMENT)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
public class Sequence extends BaseEntity {
    @JMap
    @Id
    private String id;

    @JMap
    private String name;

    @JMap
    private String description;

    @JMap
    private SequenceStatus status;

    @JMap
    private Set<SequenceUser> users;

    @DBRef(lazy = true)
    private List<SequenceStep> steps;

    public Set<SequenceUser> getUsers() {
        if (users == null) {
            users = new HashSet<>();
        }
        return users;
    }

    public List<SequenceStep> getSteps() {
        if (steps == null) {
            steps = new ArrayList<>();
        }
        return steps;
    }
}
