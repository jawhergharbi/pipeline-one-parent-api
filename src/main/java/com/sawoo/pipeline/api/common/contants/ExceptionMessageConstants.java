package com.sawoo.pipeline.api.common.contants;

public interface ExceptionMessageConstants {

    String AUTH_COMMON_PASSWORD_MATCH_EXCEPTION = "auth.common.password-must-match.exception";
    String AUTH_REGISTER_IDENTIFIER_ALREADY_EXISTS_EXCEPTION = "auth.register.identifier-already-exists.exception";
    String AUTH_REGISTER_USER_SERVICE_ERROR_EXCEPTION = "auth.register.user-service-error.exception";
    String AUTH_LOGIN_USER_DISABLE_ERROR_EXCEPTION = "auth.login.user-disable-error.exception";
    String AUTH_LOGIN_INVALID_CREDENTIALS_ERROR_EXCEPTION = "auth.login.invalid-credentials-error.exception";
    String AUTH_LOGIN_USERNAME_NOT_FOUND_ERROR_EXCEPTION = "auth.login.username-not-found-error.exception";
    String AUTH_LOGIN_USER_IDENTIFIER_NOT_FOUND_ERROR_EXCEPTION = "auth.login.user-identifier-not-found-error.exception";
    String AUTH_LOGIN_USER_DETAILS_CLASS_ERROR_EXCEPTION = "auth.login.user-details-class-error.exception";

    String CLIENT_UPDATE_CSM_MATCH_SA_EXCEPTION = "client.update-csm.match-sa.exception";
    String CLIENT_UPDATE_SA_MATCH_CSM_EXCEPTION = "client.update-sa.match-csm.exception";
    String CLIENT_UPDATE_SA_MUST_HAVE_ROLE_SA_EXCEPTION = "client.update-sa.must-have-role-sa.exception";
    String CLIENT_UPDATE_CSM_MUST_HAVE_ROLE_CSM_EXCEPTION = "client.update-csm.must-have-role-csm.exception";

    String ACCOUNT_LEAD_REMOVE_LEAD_NOT_FOUND_EXCEPTION = "account.lead.remove-lead-not-found.exception";

    String COMMON_CREATE_ENTITY_ALREADY_EXISTS_EXCEPTION = "common.create.entity-already-exists.exception";

    String COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION = "common.get-component.not-found.exception";
    String COMMON_DELETE_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION = "common.delete-component.not-found.exception";
    String COMMON_UPDATE_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION = "common.update-component.not-found.exception";
    String COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR = "common.field.can-not-be-empty-error";
    String COMMON_LIST_FIELD_CAN_NOT_BE_EMPTY_ERROR = "common.list-field.can-not-be-empty-error";
    String COMMON_FIELD_CAN_NOT_BE_NULL_ERROR = "common.field.can-not-be-null-error";
    String COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR = "common.field.can-not-be-empty-or-null-error";
    String COMMON_FIELD_CAN_NOT_EXCEED_MAX_SIZE_ERROR = "common.field.size-exceed-max-size";
    String COMMON_FIELD_CAN_NOT_BE_BELLOW_MIN_SIZE_ERROR = "common.field.size-bellow-min-size";
    String COMMON_MISSING_REQUEST_PARAM_EXCEPTION_ERROR = "common.missing-request-param.error";
    String COMMON_REFERENCE_CHILD_WAS_NOT_FOUND_ERROR = "common.reference-child.not-found.exception";
    String COMMON_DISC_ANALYSIS_LOADING_EXCEPTION = "common.disc-analysis-loading.exception";
    String COMMON_METHOD_NOT_ALLOWED_EXCEPTION = "common.method-not-allowed.exception";
    String COMMON_INTERNAL_SERVER_ERROR_EXCEPTION = "common.internal-server-error.exception";

    String LEAD_REPORT_GENERATION_INTERNAL_SERVER_EXCEPTION = "lead.report-generation.internal-server.exception";
    String LEAD_REPORT_GENERATION_STREAM_BUFFER_EMPTY_ERROR = "lead.report-generation.pdf-stream-empty.error";

    String COMPANY_CROSS_FIELD_VALIDATION_ERROR = "company.validation.cross-field.error";
    String PROSPECT_CROSS_FIELD_VALIDATION_ERROR = "prospect.validation.cross-field.error";

}
