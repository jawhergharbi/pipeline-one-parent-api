package com.sawoo.pipeline.api.dto.lead;

import com.googlecode.jmapper.annotations.JMap;
import com.sawoo.pipeline.api.dto.DiscTypeDTO;
import com.sawoo.pipeline.api.dto.company.CompanyDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeadReportDataDTO {

    @JMap(attributes = {"firstName", "lastName"})
    private String fullName;

    @JMap
    private String position;

    @JMap
    private String linkedInUrl;

    @JMap
    private String email;

    @JMap
    private String phoneNumber;

    @JMap
    private String linkedInThread;

    @JMap
    private String profilePicture;

    @JMap("extraNotes")
    private String notes;

    @JMap
    private CompanyDTO company;

    @JMap("companyComments")
    private String companyNotes;

    @JMap
    private DiscTypeDTO personality;
}
