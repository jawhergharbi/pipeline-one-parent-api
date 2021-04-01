package com.sawoo.pipeline.api.mock;

import com.sawoo.pipeline.api.dto.PersonalityDTO;
import com.sawoo.pipeline.api.dto.company.CompanyDTO;
import com.sawoo.pipeline.api.dto.lead.LeadDTO;
import com.sawoo.pipeline.api.dto.person.PersonDTO;
import com.sawoo.pipeline.api.model.common.Note;
import com.sawoo.pipeline.api.model.common.Personality;
import com.sawoo.pipeline.api.model.common.Status;
import com.sawoo.pipeline.api.model.company.Company;
import com.sawoo.pipeline.api.model.lead.Lead;
import com.sawoo.pipeline.api.model.person.Person;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class LeadMockFactory extends BaseMockFactory<LeadDTO, Lead> {

    @Getter
    private final PersonMockFactory personMockFactory;

    @Getter
    private final TodoMockFactory todoMockFactory;

    @Getter
    private final TodoAssigneeMockFactory interactionAssigneeMockFactory;

    @Override
    public String getComponentId() {
        return getFAKER().internet().uuid();
    }

    @Override
    public Lead newEntity(String id) {
        Lead entity = new Lead();
        entity.setId(id);
        entity.setLinkedInThread(getFAKER().internet().url());
        entity.setCompanyNotes(
                Note.builder()
                        .text(getFAKER().lorem().sentence(10))
                        .updated(LocalDateTime.now())
                        .build());
        entity.setLeadNotes(
                Note.builder()
                        .text(getFAKER().lorem().sentence(15))
                        .updated(LocalDateTime.now()).
                        build());
        entity.setStatus(Status.builder()
                .notes(Note.builder()
                        .text(getFAKER().lorem().sentence(20))
                        .updated(LocalDateTime.now())
                        .build())
                .value(getFAKER().number().numberBetween(0, 3))
                .updated(LocalDateTime.now())
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
    public LeadDTO newDTO(String id) {
        LocalDateTime now = LocalDateTime.now();
        LeadDTO dto = new LeadDTO();
        dto.setId(id);
        dto.setLinkedInThread(getFAKER().internet().url());
        dto.setCompanyNotes(
                Note.builder()
                        .text(getFAKER().lorem().sentence(10))
                        .updated(now)
                        .build());
        dto.setLeadNotes(
                Note.builder()
                        .text(getFAKER().lorem().sentence(15))
                        .updated(now).
                        build());
        dto.setStatus(Status.builder()
                .notes(Note.builder()
                        .text(getFAKER().lorem().sentence(20))
                        .updated(now)
                        .build())
                .value(getFAKER().number().numberBetween(0, 3))
                .updated(now)
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
    public LeadDTO newDTO(String id, LeadDTO dto) {
        return LeadDTO.builder()
                .id(id)
                .person(dto.getPerson())
                .leadNotes(dto.getLeadNotes())
                .companyNotes(dto.getCompanyNotes())
                .linkedInThread(dto.getLinkedInThread())
                .status(dto.getStatus())
                .build();
    }
}
