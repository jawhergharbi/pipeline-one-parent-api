package com.sawoo.pipeline.api.model.prospect;

public enum ProspectStatusList {

    TARGETABLE(0),
    LEAD(1),
    HOT(2),
    MQL(10),
    SQL(11),
    OPPORTUNITY(20),
    DEAD(30),
    NOT_RELEVANT(31),
    INTERESTING(32);

    private final int status;

    ProspectStatusList(int status) {
        this.status = status;
    }

    public int getStatus() {
        return this.status;
    }
}
