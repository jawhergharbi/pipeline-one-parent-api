package com.sawoo.pipeline.api.model.lead;

public enum ProspectStatus {

    FUNNEL_ON_GOING(0),
    INDIVIDUALLY_APPROACHED(1),
    FUNNEL_FINISHED(2),
    DEAD(3),
    NOT_RELEVANT(4),
    HOT(10),
    LEAD_NOT_RELEVANT(11),
    MQL(12),
    SQL(13),
    LEAD_DEAD(14);

    private final int status;

    private ProspectStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return this.status;
    }
}
