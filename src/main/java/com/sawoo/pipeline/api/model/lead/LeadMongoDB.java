package com.sawoo.pipeline.api.model.lead;

import com.googlecode.jmapper.annotations.JMap;
import com.googlecode.jmapper.annotations.JMapConversion;
import com.sawoo.pipeline.api.model.CompanyMongoDB;
import com.sawoo.pipeline.api.model.DataStoreConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cloud.gcp.data.datastore.core.mapping.Field;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = DataStoreConstants.LEAD_DOCUMENT)
public class LeadMongoDB {

    @JMap
    @Id
    private String id;

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
    @DBRef
    private CompanyMongoDB company;

    @JMap
    private LeadPersonality personality;

    @JMap
    private LocalDateTime created;

    @JMap
    private LocalDateTime updated;

    @JMapConversion(from = {"firstName", "lastName"}, to = {"fullName"})
    public String conversion(String firstName, String lastName) {
        // TODO: jmapper hack given that it's not possible to map from two source fields in to one destination field
        return String.join(" ", this.firstName, this.lastName);
    }

    @JMapConversion(from = {"salutation"}, to = {"salutation"})
    public Integer salutationConversion(Integer salutation) {
        return salutation == null ? 0 : salutation;
    }
}
