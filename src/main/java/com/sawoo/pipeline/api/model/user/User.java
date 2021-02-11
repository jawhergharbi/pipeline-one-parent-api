package com.sawoo.pipeline.api.model.user;

import com.googlecode.jmapper.annotations.JMap;
import com.googlecode.jmapper.annotations.JMapConversion;
import com.sawoo.pipeline.api.model.BaseEntity;
import com.sawoo.pipeline.api.model.DBConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
@Document(collection = DBConstants.USER_DOCUMENT)
public class User extends BaseEntity {

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
