package com.sawoo.pipeline.api.dto.lead;

import com.googlecode.jmapper.annotations.JMap;
import com.sawoo.pipeline.api.dto.PersonalityDTO;
import com.sawoo.pipeline.api.dto.company.CompanyDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeadReportDTO {

    @JMap("${prospect.firstName}")
    private String firstName;

    @JMap("${prospect.lastName}")
    private String lastName;

    @JMap("${prospect.position}")
    private String position;

    @JMap("${prospect.linkedInUrl}")
    private String linkedInUrl;

    @JMap("${prospect.email}")
    private String email;

    @JMap("${prospect.phoneNumber}")
    private String phoneNumber;

    @JMap
    private String linkedInThread;

    @JMap("${prospect.profilePicture}")
    private String profilePicture;

    @JMap("leadNotes")
    private String notes;

    @JMap("${prospect.company}")
    private CompanyDTO company;

    @JMap("companyNotes")
    private String reportCompanyNotes;

    @JMap("${prospect.personality}")
    private PersonalityDTO personality;
}
