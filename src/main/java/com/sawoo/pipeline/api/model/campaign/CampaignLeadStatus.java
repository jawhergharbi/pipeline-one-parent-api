package com.sawoo.pipeline.api.model.campaign;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.IllegalArgumentException;
import com.sawoo.pipeline.api.model.sequence.SequenceStatus;

import java.util.Arrays;

public enum CampaignLeadStatus {

    RUNNING(0),
    PAUSED(1),
    ENDED(2),
    ARCHIVED(3);

    private final int value;

    CampaignLeadStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static CampaignLeadStatus fromValue(int value) {
        return Arrays
                .stream(CampaignLeadStatus.values())
                .filter(s -> s.getValue() == value)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        ExceptionMessageConstants.COMMON_ENUM_WRONG_VALUE_ILLEGAL_ARGUMENT_EXCEPTION,
                        new Object[] {SequenceStatus.class.getSimpleName(), SequenceStatus.values(), value}));
    }
}
