package com.sawoo.pipeline.api.dto.lead;

import com.googlecode.jmapper.annotations.JMap;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeadInteractionLeadDTO {

    @JMap("${id}")
    private String leadId;

    @JMap("${person.id}")
    private String personId;

    @JMap("${person.fullName}")
    private String fullName;

    @JMap("${person.position}")
    private String position;

    @JMap("${person.linkedInUrl}")
    private String linkedInUrl;
}
