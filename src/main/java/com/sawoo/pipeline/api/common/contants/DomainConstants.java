package com.sawoo.pipeline.api.common.contants;

public interface DomainConstants {

    Integer AUTHORIZATION_PROVIDER_TYPE_EMAIL = 0;
    Integer AUTHORIZATION_PROVIDER_TYPE_ID = 1;

    enum LeadStatus {
        WARM,
        HOT,
        DEAD,
        MQL,
        SQL,
        OTHER,
        NOT_CUSTOMER
    }

    enum ClientStatus {
        ON_BOARDING,
        RUNNING,
        PAUSED,
        ENDED
    }

    enum InteractionStatus {
        SCHEDULED,
        CANCELLED,
        DONE,
        RESCHEDULED
    }

    enum InteractionType {
        LINKED_IN,
        EMAIL,
        PHONE
    }
}
