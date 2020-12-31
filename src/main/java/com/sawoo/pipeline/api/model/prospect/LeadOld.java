package com.sawoo.pipeline.api.model.prospect;

import com.googlecode.jmapper.annotations.JMap;
import com.googlecode.jmapper.annotations.JMapConversion;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.common.Note;
import com.sawoo.pipeline.api.model.common.Personality;
import com.sawoo.pipeline.api.model.common.Status;
import com.sawoo.pipeline.api.model.company.Company;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cloud.gcp.data.datastore.core.mapping.Descendants;
import org.springframework.cloud.gcp.data.datastore.core.mapping.Entity;
import org.springframework.cloud.gcp.data.datastore.core.mapping.Field;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Reference;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = DBConstants.LEAD_DOCUMENT)
public class LeadOld {

    @JMap
    @Id
    private Long id;

    @JMap
    private Integer salutation;

    @JMap
    private String firstName;

    @JMap
    private String lastName;

    @JMap
    private String position;

    @JMap
    private String email;

    @JMap
    @Field(name = "phone_number")
    private String phoneNumber;

    @JMap
    @Field(name = "linked_in_url")
    private String linkedInUrl;

    @JMap
    @Field(name = "linked_in_thread")
    private String linkedInThread;

    @JMap
    @Field(name = "profile_picture")
    private String profilePicture;

    @JMap
    @Field(name = "extra_notes")
    private Note extraNotes;

    @JMap
    @Reference
    private Company company;

    @JMap
    @Field(name = "company_comments")
    private Note companyComments;

    @JMap
    private Status status;

    @JMap
    private Personality personality;

    @Descendants
    private List<LeadInteractionOld> interactions;
    public List<LeadInteractionOld> getInteractions() {
        if (interactions == null) {
            interactions = new ArrayList<>();
        }
        return interactions;
    }

    @JMap
    private LocalDateTime created;

    @JMap
    private LocalDateTime updated;

    @JMapConversion(from = {"firstName", "lastName"}, to = {"fullName"})
    public String conversion(String firstName, String lastName) {
        // TODO: jmapper hack given that it's not possible to map from two source fields in to one destination field
        return String.join(" ", this.firstName, this.lastName);
    }

    @JMapConversion(from = {"companyComments"}, to = {"companyNotes"})
    public String companyNotesConversion(Note companyComments) {
        return companyComments == null ? "" : companyComments.getText();
    }

    @JMapConversion(from = {"extraNotes"}, to = {"notes"})
    public String extraNotesConversion(Note extraNotes) {
        return extraNotes == null ? "" : extraNotes.getText();
    }
}
