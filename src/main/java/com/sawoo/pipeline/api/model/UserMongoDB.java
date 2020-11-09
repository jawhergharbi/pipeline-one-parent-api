package com.sawoo.pipeline.api.model;

import com.googlecode.jmapper.annotations.JMap;
import com.googlecode.jmapper.annotations.JMapConversion;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.cloud.gcp.data.datastore.core.mapping.Field;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = DataStoreConstants.USER_DOCUMENT)
public class UserMongoDB extends EntityBase {

    @JMap
    @Id
    private String id;

    @JMap
    @Indexed
    private String email;

    @JMap
    @Indexed
    private String fullName;

    @JMap
    private Boolean active;

    @JMap
    @Indexed
    private Set<String> roles;

    @JMap
    @Field(name = "last_login")
    private LocalDateTime lastLogin;

    @ToString.Exclude
    private String password;

    @JMapConversion(from = {"active"}, to = {"active"})
    public Boolean conversion(Boolean active) {
        return active != null ? active : Boolean.TRUE;
    }

    public Set<String> getRoles() {
        if (roles == null) {
            roles = new HashSet<>();
        }
        return roles;
    }
}
