package com.sawoo.pipeline.api.model.interaction;

public enum InteractionStatusList {

    SCHEDULED(0),
    CANCELLED(1),
    DONE(2),
    RESCHEDULED(3);

    private final int status;

    private InteractionStatusList(int status) {
        this.status = status;
    }

    public int getStatus() {
        return this.status;
    }
}
