package com.sawoo.pipeline.api.model.user;

public enum UserTokenType {
    PASSWORD(0),
    SESSION(1),
    SESSION_REFRESH(2);

    private final int type;

    UserTokenType(int type) {
        this.type = type;
    }

    public int getType() {
        return this.type;
    }
}
