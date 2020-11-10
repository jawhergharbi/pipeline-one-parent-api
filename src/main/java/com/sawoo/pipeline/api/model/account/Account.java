package com.sawoo.pipeline.api.model.account;

import com.googlecode.jmapper.annotations.JMap;
import com.sawoo.pipeline.api.model.*;
import com.sawoo.pipeline.api.model.common.Note;
import lombok.*;
import org.springframework.cloud.gcp.data.datastore.core.mapping.Field;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = DataStoreConstants.ACCOUNT_DOCUMENT)
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class Account extends BaseEntity {

    @JMap
    @Id
    private String id;

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
    @Indexed
    @Field(name = "linked_in_url")
    private String linkedInUrl;

    @JMap
    private Note notes;

    @JMap
    @DBRef
    private Company company;

    @JMap
    private Status status;

    @JMap
    @DBRef
    private User assistant;

    @JMap
    @DBRef
    private User manager;
}
