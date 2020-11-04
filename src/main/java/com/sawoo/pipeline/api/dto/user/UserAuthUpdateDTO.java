package com.sawoo.pipeline.api.dto.user;

import com.googlecode.jmapper.annotations.JMap;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class UserAuthUpdateDTO extends UserAuthDTO {

    @ToString.Exclude
    private String password;
}
