package com.sawoo.pipeline.api.model.account;

public enum AccountStatus {

    ON_BOARDING(0),
    RUNNING(1),
    PAUSED(2),
    ENDED(3);

    private int value;

    AccountStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
