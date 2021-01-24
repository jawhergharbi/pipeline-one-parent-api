package com.sawoo.pipeline.api.model.person;

import com.googlecode.jmapper.annotations.JMap;
import com.sawoo.pipeline.api.model.BaseEntity;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.common.Personality;
import com.sawoo.pipeline.api.model.company.Company;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
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
    private String phoneNumber;

    @JMap
    @Indexed
    private String linkedInUrl;

    @JMap
    private String profilePicture;

    @JMap
    @DBRef
    private Company company;

    @JMap
    private Personality personality;
}
