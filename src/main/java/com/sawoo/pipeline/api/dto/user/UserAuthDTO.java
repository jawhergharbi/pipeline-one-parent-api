package com.sawoo.pipeline.api.dto.user;

import com.googlecode.jmapper.annotations.JMap;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAuthDTO {

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

    @JMap
    private LocalDateTime lastLogin;

    @JMap
    private String email;
}
