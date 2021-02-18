package com.sawoo.pipeline.api.model.sequence;

public enum SequenceStatus {

    IN_PROGRESS(0),
    TO_BE_REVIEWED(1),
    READY(2),
    ARCHIVED(3);

    private int value;

    SequenceStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
