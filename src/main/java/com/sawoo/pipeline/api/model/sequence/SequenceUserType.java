package com.sawoo.pipeline.api.model.sequence;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = SequenceUserTypeDeserializer.class)
public enum SequenceUserType {

    OWNER(0),
    EDITOR(10),
    VIEWER(20);

    private final int type;

    private SequenceUserType(int type) {
        this.type = type;
    }

    public int getType() {
        return this.type;
    }
}
