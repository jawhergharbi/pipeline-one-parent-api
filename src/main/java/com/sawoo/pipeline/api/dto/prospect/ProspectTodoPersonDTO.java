package com.sawoo.pipeline.api.dto.prospect;

import com.googlecode.jmapper.annotations.JMap;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProspectTodoPersonDTO {

    @JMap("${id}")
    private String prospectId;

    @JMap("${person.id}")
    private String personId;

    @JMap("${person.fullName}")
    private String fullName;

    @JMap("${person.position}")
    private String position;

    @JMap("${person.linkedInUrl}")
    private String linkedInUrl;
}
