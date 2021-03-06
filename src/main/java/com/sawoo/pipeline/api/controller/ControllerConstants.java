package com.sawoo.pipeline.api.controller;

public interface ControllerConstants {

    String API_BASE_URI = "/api/";

    String ACCOUNT_CONTROLLER_RESOURCE_NAME = "accounts";
    String ACCOUNT_CONTROLLER_API_BASE_URI = API_BASE_URI + ACCOUNT_CONTROLLER_RESOURCE_NAME;
    String PERSON_CONTROLLER_RESOURCE_NAME = "persons";
    String PERSON_CONTROLLER_API_BASE_URI = API_BASE_URI + PERSON_CONTROLLER_RESOURCE_NAME;
    String COMPANY_CONTROLLER_RESOURCE_NAME = "companies";
    String COMPANY_CONTROLLER_API_BASE_URI = API_BASE_URI + COMPANY_CONTROLLER_RESOURCE_NAME;
    String CAMPAIGN_CONTROLLER_RESOURCE_NAME = "campaigns";
    String CAMPAIGN_CONTROLLER_API_BASE_URI = API_BASE_URI + CAMPAIGN_CONTROLLER_RESOURCE_NAME;
    String SEQUENCE_CONTROLLER_RESOURCE_NAME = "sequences";
    String SEQUENCE_CONTROLLER_API_BASE_URI = API_BASE_URI + SEQUENCE_CONTROLLER_RESOURCE_NAME;
    String AUTH_CONTROLLER_RESOURCE_NAME = "auth";
    String USER_CONTROLLER_API_BASE_URI = API_BASE_URI + AUTH_CONTROLLER_RESOURCE_NAME;
    String PROSPECT_CONTROLLER_RESOURCE_NAME = "leads";
    String PROSPECT_CONTROLLER_RESOURCE_PATH_VARIABLE_NAME = "leadId";
    String PROSPECT_CONTROLLER_API_BASE_URI = API_BASE_URI + PROSPECT_CONTROLLER_RESOURCE_NAME;
    String TODO_CONTROLLER_RESOURCE_NAME = "todos";
    String TODO_CONTROLLER_API_BASE_URI = API_BASE_URI + TODO_CONTROLLER_RESOURCE_NAME;
    String EMAIL_CONTROLLER_API_BASE_URI = API_BASE_URI + "emails";
}
