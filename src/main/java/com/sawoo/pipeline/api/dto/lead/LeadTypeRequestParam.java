package com.sawoo.pipeline.api.dto.lead;

public enum LeadTypeRequestParam {
    PROSPECT(0),
    LEAD(1);

    private final int type;

    LeadTypeRequestParam(int type) {
        this.type = type;
    }

    public int getType() {
        return this.type;
    }
}
