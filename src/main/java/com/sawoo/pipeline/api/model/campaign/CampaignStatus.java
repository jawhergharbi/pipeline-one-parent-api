package com.sawoo.pipeline.api.model.campaign;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.IllegalArgumentException;
import com.sawoo.pipeline.api.model.sequence.SequenceStatus;

import java.util.Arrays;

public enum CampaignStatus {

    UNDER_CONSTRUCTION(0),
    RUNNING(1),
    PAUSED(2),
    ENDED(3),
    ARCHIVED(4);

    private final int value;

    CampaignStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static CampaignStatus fromValue(int value) {
        return Arrays
                .stream(CampaignStatus.values())
                .filter(s -> s.getValue() == value)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        ExceptionMessageConstants.COMMON_ENUM_WRONG_VALUE_ILLEGAL_ARGUMENT_EXCEPTION,
                        new Object[] {SequenceStatus.class.getSimpleName(), SequenceStatus.values(), value}));
    }
}
