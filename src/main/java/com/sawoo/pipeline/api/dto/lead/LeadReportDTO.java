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

    @JMap("${person.firstName}")
    private String firstName;

    @JMap("${person.lastName}")
    private String lastName;

    @JMap("${person.position}")
    private String position;

    @JMap("${person.linkedInUrl}")
    private String linkedInUrl;

    @JMap("${person.email}")
    private String email;

    @JMap("${person.phoneNumber}")
    private String phoneNumber;

    @JMap
    private String linkedInThread;

    @JMap("${person.profilePicture}")
    private String profilePicture;

    @JMap("leadNotes")
    private String notes;

    @JMap("${person.company}")
    private CompanyDTO company;

    @JMap("companyNotes")
    private String reportCompanyNotes;

    @JMap("${person.personality}")
    private PersonalityDTO personality;
}
