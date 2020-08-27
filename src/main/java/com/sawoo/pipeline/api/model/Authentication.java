package com.sawoo.pipeline.api.model;

import com.googlecode.jmapper.annotations.JMap;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.cloud.gcp.data.datastore.core.mapping.Entity;
import org.springframework.cloud.gcp.data.datastore.core.mapping.Field;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@Entity(name = "authentication")
public class Authentication {

    @JMap
    @Id
    private String id;

    @JMap
    private String identifier;

    @ToString.Exclude
    private String password;

    @JMap
    @Field(name = "provider_type")
    private Integer providerType;

    @JMap
    @Field(name = "signed_up")
    private LocalDateTime signedUp;

    @JMap
    @Field(name = "last_login")
    private LocalDateTime lastLogin;

    @JMap
    private LocalDateTime updated;
}
