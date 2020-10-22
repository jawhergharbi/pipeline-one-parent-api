package com.sawoo.pipeline.api.common.contants;

public interface DomainConstants {

    Integer AUTHORIZATION_PROVIDER_TYPE_EMAIL = 0;
    Integer AUTHORIZATION_PROVIDER_TYPE_ID = 1;

    Integer SALUTATION_EMPTY = 0;
    Integer SALUTATION_MISTER = 1;
    Integer SALUTATION_MISS = 2;
    Integer SALUTATION_DOCTOR_HIM = 3;
    Integer SALUTATION_DOCTOR_HER = 4;
    Integer SALUTATION_PROFESSOR_HIM = 5;
    Integer SALUTATION_PROFESSOR_HER = 6;

    String PROSPECT_REPORT_TYPE_REPORT = "report";
    String PROSPECT_REPORT_TYPE_FULL_REPORT = "report-full";


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
