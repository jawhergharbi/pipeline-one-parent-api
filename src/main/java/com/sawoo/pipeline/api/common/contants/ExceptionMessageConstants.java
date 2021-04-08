package com.sawoo.pipeline.api.common.contants;

public interface ExceptionMessageConstants {

    String AUTH_COMMON_PASSWORD_MATCH_EXCEPTION = "auth.common.password-must-match.exception";
    String AUTH_REGISTER_USER_SERVICE_ERROR_EXCEPTION = "auth.register.user-service-error.exception";
    String AUTH_LOGIN_USER_DISABLE_ERROR_EXCEPTION = "auth.login.user-disable-error.exception";
    String AUTH_LOGIN_INVALID_CREDENTIALS_ERROR_EXCEPTION = "auth.login.invalid-credentials-error.exception";
    String AUTH_LOGIN_USERNAME_NOT_FOUND_ERROR_EXCEPTION = "auth.login.username-not-found-error.exception";
    String AUTH_LOGIN_USER_IDENTIFIER_NOT_FOUND_ERROR_EXCEPTION = "auth.login.user-identifier-not-found-error.exception";
    String AUTH_LOGIN_USER_DETAILS_CLASS_ERROR_EXCEPTION = "auth.login.user-details-class-error.exception";
    String AUTH_RESET_PASSWORD_USER_EMAIL_NOT_FOUND_ERROR_EXCEPTION = "auth.reset-password.user-email-not-found-error.exception";
    String AUTH_RESET_PASSWORD_PASSWORD_MATCH_EXCEPTION = "auth.reset-password.password-must-match.exception";
    String AUTH_RESET_PASSWORD_CONFIRM_TOKEN_NOT_FOUND_ERROR_EXCEPTION = "auth.reset-password.confirm-token-not-found-error.exception";
    String AUTH_RESET_PASSWORD_CONFIRM_TOKEN_EXPIRED_ERROR_EXCEPTION = "auth.reset-password.confirm-token-expired-error.exception";
    String AUTH_TOKEN_EMAIL_NOT_FOUND_ERROR_EXCEPTION = "auth.token.email-not-found-error.exception";

    String ACCOUNT_PROSPECT_REMOVE_PROSPECT_NOT_FOUND_EXCEPTION = "account.prospect.remove-prospect-not-found.exception";
    String ACCOUNT_PROSPECT_CREATE_PROSPECT_ALREADY_ADDED_EXCEPTION = "account.prospect.create-prospect-already-added.exception";

    String COMMON_CREATE_ENTITY_ALREADY_EXISTS_EXCEPTION = "common.create.entity-already-exists.exception";

    String COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION = "common.get-component.not-found.exception";
    String COMMON_DELETE_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION = "common.delete-component.not-found.exception";
    String COMMON_UPDATE_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION = "common.update-component.not-found.exception";
    String COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR = "common.field.can-not-be-empty.error";
    String COMMON_FIELD_MUST_BE_AN_EMAIL_ERROR = "common.field.must-be-an-email.error";
    String COMMON_LIST_FIELD_CAN_NOT_BE_EMPTY_ERROR = "common.list-field.can-not-be-empty.error";
    String COMMON_FIELD_CAN_NOT_BE_NULL_ERROR = "common.field.can-not-be-null.error";
    String COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR = "common.field.can-not-be-empty-or-null.error";
    String COMMON_FIELD_CAN_NOT_EXCEED_MAX_SIZE_ERROR = "common.field.size-exceed-max-size";
    String COMMON_FIELD_CAN_NOT_BE_BELLOW_MIN_SIZE_ERROR = "common.field.size-bellow-min-size";
    String COMMON_MISSING_REQUEST_PARAM_EXCEPTION_ERROR = "common.missing-request-param.error";
    String COMMON_DISC_ANALYSIS_LOADING_EXCEPTION = "common.disc-analysis-loading.exception";
    String COMMON_METHOD_NOT_ALLOWED_EXCEPTION = "common.method-not-allowed.exception";
    String COMMON_INTERNAL_SERVER_ERROR_EXCEPTION = "common.internal-server-error.exception";
    String COMMON_ILLEGAL_ENUMERATION_VALUE_EXCEPTION = "common.enumeration-illegal-value.exception";

    String PROSPECT_REPORT_GENERATION_INTERNAL_SERVER_EXCEPTION = "prospect.report-generation.internal-server.exception";
    String PROSPECT_REPORT_GENERATION_STREAM_BUFFER_EMPTY_ERROR = "prospect.report-generation.pdf-stream-empty.error";
    String PROSPECT_TODO_ADD_PROSPECT_SLOT_ALREADY_SCHEDULED_EXCEPTION = "prospect.todo-add-todo-already-scheduled.exception";
    String PROSPECT_SEQUENCE_TODO_PERSONALITY_NOT_ASSIGNED_EXCEPTION = "prospect.sequence-todo-personality-not-assigned.exception";
    String PROSPECT_PROSPECT_ACCOUNT_NOT_FOUND_EXCEPTION = "prospect.prospect-account-not-found.exception";
    String PROSPECT_PROSPECT_ACCOUNT_ASSIGNEE_NOT_FOUND_EXCEPTION = "prospect.prospect-account-assignee-not-found.exception";
    String PROSPECT_PROSPECT_ACCOUNT_NO_ASSIGNEE_FOUND_EXCEPTION = "prospect.prospect-account-no-assignee-found.exception";

    String COMPANY_CROSS_FIELD_VALIDATION_ERROR = "company.validation.cross-field.error";
    String PERSON_CROSS_FIELD_VALIDATION_ERROR = "person.validation.cross-field.error";

    String MAIL_EXCEPTION_SEND_MESSAGE = "mail.send-message.exception";
    String MAIL_EXCEPTION_SEND_MESSAGE_WITH_ATTACHMENT = "mail.send-message-with-attachment.exception";
    String MAIL_EXCEPTION_SEND_MESSAGE_WITH_TEMPLATE = "mail.send-message-with-template.exception";

    String COMMON_ENUM_WRONG_VALUE_ILLEGAL_ARGUMENT_EXCEPTION = "common.enum-wrong-value.exception";

    String SEQUENCE_UPDATE_USER_ID_NOT_INFORMED_EXCEPTION = "sequence.update.user-id-not-informed.exception";
    String SEQUENCE_UPDATE_DELETE_USER_NOT_FOUND_EXCEPTION = "sequence.update.delete-user-not-found.exception";
    String SEQUENCE_UPDATE_DELETE_USER_LIST_EMPTY_EXCEPTION = "sequence.update.delete-list-empty.exception";
    String SEQUENCE_UPDATE_DELETE_USER_OWNER_EXCEPTION = "sequence.update.delete-user-owner.exception";
    String SEQUENCE_CREATE_USER_OWNER_NOT_SPECIFIED_EXCEPTION = "sequence.create.user-owner-not-informed.exception";
    String SEQUENCE_STEP_ADD_STEP_POSITION_AND_PERSONALITY_ALREADY_FILLED_EXCEPTION = "sequence.step.add-step-position-and-personality-already-filled.exception";
    String SEQUENCE_STEP_UPDATE_STEP_NOT_FOUND_IN_THE_SEQUENCE_EXCEPTION = "sequence.step.update-step-not-found-in-the-sequence.exception";
    String SEQUENCE_STEP_UPDATE_STEP_POSITION_OR_PERSONALITY_CANT_CHANGE_EXCEPTION = "sequence.step.update-step-position-or-personality-cant-change.exception";

    String CAMPAIGN_ADD_PROSPECT_ALREADY_ADDED_EXCEPTION = "campaign.campaign-prospect.prospect-already-added.exception";
    String CAMPAIGN_REMOVE_PROSPECT_NOT_PRESENT_EXCEPTION = "campaign.campaign-prospect.remove-prospect-not-present.exception";
    String CAMPAIGN_UPDATE_PROSPECT_NOT_PRESENT_EXCEPTION = "campaign.campaign-prospect.update-prospect-not-present.exception";


    String REPOSITORY_EXCEPTION_DATA_CONVERSATION_ERROR_EXCEPTION = "repository.data-conversation.exception";

}
