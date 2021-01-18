package com.sawoo.pipeline.api.integration;

import lombok.Data;

import java.util.List;

@Data
public class ExpectedResult<M> {

    private String key;
    private List<M> values;
    private List<String> fieldsToBeChecked;

}
