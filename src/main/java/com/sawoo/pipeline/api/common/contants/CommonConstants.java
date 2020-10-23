package com.sawoo.pipeline.api.common.contants;

public interface CommonConstants {
    String AUTHORITIES_KEY = "roles";
    String USER_ID_KEY = "id";
    long JWT_TOKEN_VALIDITY = 5 * 60 * 60;

    int REPORT_PDF_FILE = 0;
    int REPORT_PDF_STREAM = 1;
}
