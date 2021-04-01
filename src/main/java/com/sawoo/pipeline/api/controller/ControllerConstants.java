package com.sawoo.pipeline.api.controller;

public interface ControllerConstants {

    String API_BASE_URI = "/api/";

    String ACCOUNT_CONTROLLER_API_BASE_URI = "/api/accounts";
    String PERSON_CONTROLLER_RESOURCE_NAME = "persons";
    String PERSON_CONTROLLER_API_BASE_URI = API_BASE_URI + PERSON_CONTROLLER_RESOURCE_NAME;
    String COMPANY_CONTROLLER_API_BASE_URI = "/api/companies";
    String CAMPAIGN_CONTROLLER_API_BASE_URI = "/api/campaigns";
    String SEQUENCE_CONTROLLER_RESOURCE_NAME = "sequences";
    String SEQUENCE_CONTROLLER_API_BASE_URI = API_BASE_URI + SEQUENCE_CONTROLLER_RESOURCE_NAME;
    String USER_CONTROLLER_API_BASE_URI = "/api/auth";
    String LEAD_CONTROLLER_API_BASE_URI = "/api/leads";
    String TODO_CONTROLLER_RESOURCE_NAME = "todos";
    String TODO_CONTROLLER_API_BASE_URI = API_BASE_URI + TODO_CONTROLLER_RESOURCE_NAME;
    String EMAIL_CONTROLLER_API_BASE_URI = "/api/emails";
}
