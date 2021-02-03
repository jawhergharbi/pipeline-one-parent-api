package com.sawoo.pipeline.api.model.user;

public enum UserTokenType {
    RESET_PASSWORD(0),
    ACTIVATE_ACCOUNT(1),
    SESSION(2),
    SESSION_REFRESH(3);

    private final int type;

    UserTokenType(int type) {
        this.type = type;
    }

    public int getType() {
        return this.type;
    }
}
