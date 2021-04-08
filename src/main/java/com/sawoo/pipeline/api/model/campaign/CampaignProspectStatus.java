package com.sawoo.pipeline.api.model.campaign;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.IllegalArgumentException;
import com.sawoo.pipeline.api.model.sequence.SequenceStatus;

import java.util.Arrays;

public enum CampaignProspectStatus {

    RUNNING(0),
    PAUSED(1),
    ENDED(2),
    ARCHIVED(3),
    INDIVIDUAL(4);

    private final int value;

    CampaignProspectStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static CampaignProspectStatus fromValue(int value) {
        return Arrays
                .stream(CampaignProspectStatus.values())
                .filter(s -> s.getValue() == value)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        ExceptionMessageConstants.COMMON_ENUM_WRONG_VALUE_ILLEGAL_ARGUMENT_EXCEPTION,
                        new Object[] {SequenceStatus.class.getSimpleName(), SequenceStatus.values(), value}));
    }
}
