package com.sawoo.pipeline.api.dto.prospect;

public enum ProspectType {
    PROSPECT(0),
    LEAD(1);

    private final int type;

    private ProspectType(int type) {
        this.type = type;
    }

    public int getType() {
        return this.type;
    }
}
