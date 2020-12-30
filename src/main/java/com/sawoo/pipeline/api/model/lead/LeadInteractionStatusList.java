package com.sawoo.pipeline.api.model.lead;

public enum LeadInteractionStatusList {

    SCHEDULED(0),
    CANCELLED(1),
    DONE(2),
    RESCHEDULED(3);

    private final int status;

    private LeadInteractionStatusList(int status) {
        this.status = status;
    }

    public int getStatus() {
        return this.status;
    }
}
