package com.sawoo.pipeline.api.model;

import com.googlecode.jmapper.annotations.JMap;
import com.googlecode.jmapper.annotations.JMapConversion;
import com.sawoo.pipeline.api.model.client.Client;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.cloud.gcp.data.datastore.core.mapping.Entity;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Reference;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@Entity(name = DataStoreConstants.USER_ENTITY_ENTITY)
public class User {

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

    @Reference
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private List<Client> clients;

    @JMapConversion(from = {"active"}, to = {"active"})
    public Boolean conversion(Boolean active) {
        return active != null ? active : Boolean.TRUE;
    }

    public List<Client> getClients() {
        if (clients == null) {
            clients = new ArrayList<>();
        }
        return clients;
    }

    public Set<String> getRoles() {
        if (roles == null) {
            roles = new HashSet<>();
        }
        return roles;
    }
}
