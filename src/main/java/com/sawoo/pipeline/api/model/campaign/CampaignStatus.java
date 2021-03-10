package com.sawoo.pipeline.api.model.campaign;

public enum CampaignStatus {

    UNDER_CONSTRUCTION(0),
    RUNNING(1),
    PAUSED(2),
    ENDED(3),
    ARCHIVED(4);

    private int value;

    CampaignStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
