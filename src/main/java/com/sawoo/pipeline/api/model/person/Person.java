package com.sawoo.pipeline.api.model.person;

import com.googlecode.jmapper.annotations.JMap;
import com.sawoo.pipeline.api.model.BaseEntity;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.common.Personality;
import com.sawoo.pipeline.api.model.company.Company;
import lombok.*;
import org.springframework.cloud.gcp.data.datastore.core.mapping.Field;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = DBConstants.PERSON_DOCUMENT)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
public class Person extends BaseEntity {

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
    @Indexed
    private String fullName;

    @JMap
    private String position;

    @JMap
    private String email;

    @JMap
    @Field(name = "phone_number")
    private String phoneNumber;

    @JMap
    @Field(name = "linked_in_url")
    @Indexed
    private String linkedInUrl;

    @JMap
    @Field(name = "profile_picture")
    private String profilePicture;

    @JMap
    @DBRef
    private Company company;

    @JMap
    private Personality personality;
}