package com.sawoo.pipeline.api.dto.prospect;

public enum ProspectTypeRequestParam {
    PROSPECT(0),
    LEAD(1);

    private final int type;

    ProspectTypeRequestParam(int type) {
        this.type = type;
    }

    public int getType() {
        return this.type;
    }
}
