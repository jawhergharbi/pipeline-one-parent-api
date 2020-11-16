package com.sawoo.pipeline.api.model;

import com.googlecode.jmapper.annotations.JMap;
import com.googlecode.jmapper.annotations.JMapConversion;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cloud.gcp.data.datastore.core.mapping.Entity;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = DBConstants.USER_DOCUMENT)
public class UserOld {

    @JMap
    @Id
    private String id;

    @JMap
    private String fullName;

    @JMap
    private Boolean active;

    @JMap
    private Set<String> roles;

    @JMap
    private LocalDateTime created;

    @JMap
    private LocalDateTime updated;

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
