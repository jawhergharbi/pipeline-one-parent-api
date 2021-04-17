package com.sawoo.pipeline.api.model.todo;

import com.googlecode.jmapper.annotations.JMap;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class TodoSearch {

    @JMap
    private List<String> componentIds;

    @JMap
    private List<String> accountIds;

    @JMap
    private List<Integer> status;

    @JMap
    private List<Integer> types;

    @JMap
    private List<String> sourceId;

    @JMap
    private List<Integer> sourceType;

}
