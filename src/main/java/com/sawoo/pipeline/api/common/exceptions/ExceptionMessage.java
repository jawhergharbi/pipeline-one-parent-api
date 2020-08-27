package com.sawoo.pipeline.api.common.exceptions;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import java.util.List;

@Getter
public class ExceptionMessage {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<String> messages;

    public ExceptionMessage(List<String> messages) {
        this.messages = messages;
    }

    public ExceptionMessage(String message) {
        this.message = message;
    }
}
