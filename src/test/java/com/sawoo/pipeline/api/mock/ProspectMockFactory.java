package com.sawoo.pipeline.api.mock;

import com.sawoo.pipeline.api.dto.PersonalityDTO;
import com.sawoo.pipeline.api.dto.company.CompanyDTO;
import com.sawoo.pipeline.api.dto.prospect.ProspectDTO;
import com.sawoo.pipeline.api.dto.person.PersonDTO;
import com.sawoo.pipeline.api.model.common.Note;
import com.sawoo.pipeline.api.model.common.Personality;
import com.sawoo.pipeline.api.model.common.Status;
import com.sawoo.pipeline.api.model.company.Company;
import com.sawoo.pipeline.api.model.prospect.Prospect;
import com.sawoo.pipeline.api.model.person.Person;
import com.sawoo.pipeline.api.model.prospect.ProspectQualification;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class ProspectMockFactory extends BaseMockFactory<ProspectDTO, Prospect> {

    @Getter
    private final PersonMockFactory personMockFactory;

    @Getter
    private final TodoMockFactory todoMockFactory;

    @Getter
    private final TodoAssigneeMockFactory todoAssigneeMockFactory;

    @Override
    public String getComponentId() {
        return getFAKER().internet().uuid();
    }

    @Override
    public Prospect newEntity(String id) {
        Prospect entity = new Prospect();
        entity.setId(id);
        entity.setLinkedInThread(getFAKER().internet().url());
        entity.setCompanyNotes(
                Note.builder()
                        .text(getFAKER().lorem().sentence(10))
                        .updated(LocalDateTime.now())
                        .build());
        entity.setProspectNotes(
                Note.builder()
                        .text(getFAKER().lorem().sentence(15))
                        .updated(LocalDateTime.now()).
                        build());
        entity.setQualification(Status.builder()
                .notes(Note.builder()
                        .text(getFAKER().lorem().sentence(20))
                        .updated(LocalDateTime.now())
                        .build())
                .value(getFAKER().number().numberBetween(0, 3))
                .build());
        entity.setPerson(Person.builder()
                .email(getFAKER().internet().emailAddress())
                .firstName(getFAKER().name().firstName())
                .lastName(getFAKER().name().lastName())
                .linkedInUrl(getFAKER().internet().url())
                .position(getFAKER().company().profession())
                .salutation(1)
                .phoneNumber(getFAKER().phoneNumber().phoneNumber())
                .profilePicture(getFAKER().internet().url())
                .personality(Personality.builder().type(1).build())
                .company(
                        Company.builder()
                                .name(getFAKER().company().name())
                                .headcount(1000)
                                .url(getFAKER().company().url())
                                .build()
                )
                .build());
        return entity;
    }

    @Override
    public ProspectDTO newDTO(String id) {
        LocalDateTime now = LocalDateTime.now();
        ProspectDTO dto = new ProspectDTO();
        dto.setId(id);
        dto.setLinkedInThread(getFAKER().internet().url());
        dto.setCompanyNotes(
                Note.builder()
                        .text(getFAKER().lorem().sentence(10))
                        .updated(now)
                        .build());
        dto.setProspectNotes(
                Note.builder()
                        .text(getFAKER().lorem().sentence(15))
                        .updated(now).
                        build());
        dto.setQualification(Status.builder()
                .notes(Note.builder()
                        .text(getFAKER().lorem().sentence(20))
                        .updated(now)
                        .build())
                .value(ProspectQualification.TARGETABLE.getValue())
                .build());
        dto.setPerson(PersonDTO
                .builder()
                .email(getFAKER().internet().emailAddress())
                .firstName(getFAKER().name().firstName())
                .lastName(getFAKER().name().lastName())
                .linkedInUrl(getFAKER().internet().url())
                .position(getFAKER().company().profession())
                .salutation(1)
                .phoneNumber(getFAKER().phoneNumber().phoneNumber())
                .profilePicture(getFAKER().internet().url())
                .personality(PersonalityDTO.builder().type(1).build())
                .company(CompanyDTO
                        .builder()
                        .name(getFAKER().company().name())
                        .headcount(1000)
                        .url(getFAKER().company().url())
                        .build())
                .build());
        dto.setCreated(now);
        dto.setUpdated(now);
        return dto;
    }

    @Override
    public ProspectDTO newDTO(String id, ProspectDTO dto) {
        return ProspectDTO.builder()
                .id(id)
                .person(dto.getPerson())
                .prospectNotes(dto.getProspectNotes())
                .companyNotes(dto.getCompanyNotes())
                .linkedInThread(dto.getLinkedInThread())
                .qualification(dto.getQualification())
                .build();
    }
}
