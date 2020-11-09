package com.sawoo.pipeline.api.model.prospect;

import com.googlecode.jmapper.annotations.JMap;
import com.sawoo.pipeline.api.model.CompanyMongoDB;
import com.sawoo.pipeline.api.model.DataStoreConstants;
import com.sawoo.pipeline.api.model.EntityBase;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cloud.gcp.data.datastore.core.mapping.Field;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = DataStoreConstants.PROSPECT_DOCUMENT)
public class Prospect extends EntityBase {

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
    @Field(name = "linked_in_thread")
    private String linkedInThread;

    @JMap
    @Field(name = "profile_picture")
    private String profilePicture;

    @JMap
    @DBRef
    private CompanyMongoDB company;

    @JMap
    private Personality personality;
}
