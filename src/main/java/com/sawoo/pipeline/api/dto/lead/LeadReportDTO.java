package com.sawoo.pipeline.api.dto.lead;

import com.googlecode.jmapper.annotations.JMap;
import com.sawoo.pipeline.api.model.common.Note;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class LeadReportDTO extends LeadBaseDTO {

    @JMap
    private String email;

    @JMap
    private String phoneNumber;

    @JMap
    private Note extraNotes;

    @JMap
    private Note companyComments;
}
