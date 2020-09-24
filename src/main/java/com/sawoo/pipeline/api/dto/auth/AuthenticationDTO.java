package com.sawoo.pipeline.api.dto.auth;

import com.googlecode.jmapper.annotations.JMap;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class AuthenticationDTO {

    @JMap
    private String id;

    @JMap
    private String identifier;

    @JMap
    private Integer providerType;

    @JMap
    private LocalDateTime signedUp;

    @JMap
    private LocalDateTime updated;
}
