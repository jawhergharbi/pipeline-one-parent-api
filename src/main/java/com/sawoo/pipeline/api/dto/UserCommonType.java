package com.sawoo.pipeline.api.dto;

public enum UserCommonType {

    USER(0),
    PROSPECT(1);

    private final int type;

    UserCommonType(int type) {
        this.type = type;
    }

    public int getType() {
        return this.type;
    }
}
