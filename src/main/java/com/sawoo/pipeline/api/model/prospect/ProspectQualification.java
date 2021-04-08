package com.sawoo.pipeline.api.model.prospect;

public enum ProspectQualification {

    TARGETABLE(0),
    LEAD(1),
    HOT(2),
    MQL(10),
    SQL(11),
    OPPORTUNITY(20),
    DEAD(30),
    NOT_RELEVANT(31),
    INTERESTING(32);

    private final int value;

    ProspectQualification(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }
}
