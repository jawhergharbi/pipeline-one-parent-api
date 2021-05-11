package com.sawoo.pipeline.api.dto.prospect;

import com.googlecode.jmapper.annotations.JMap;
import com.sawoo.pipeline.api.dto.PersonalityDTO;
import com.sawoo.pipeline.api.dto.company.CompanyDTO;
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

    @JMap("${person.profilePicture}")
    private String profilePicture;

    @JMap("${person.personality}")
    private PersonalityDTO personality;

    @JMap("${person.linkedInUrl}")
    private String linkedInUrl;

    @JMap("${linkedInThread}")
    private String linkedInChat;

    @JMap("${person.company}")
    private CompanyDTO company;
}
