package com.sawoo.pipeline.api.dto.account;

import com.googlecode.jmapper.annotations.JMap;
import com.sawoo.pipeline.api.dto.BaseEntityDTO;
import com.sawoo.pipeline.api.dto.company.CompanyDTO;
import com.sawoo.pipeline.api.dto.user.UserAuthDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
@Builder(toBuilder = true)
public class AccountLeadDTO extends BaseEntityDTO {

    @JMap
    private String id;

    @JMap
    private String fullName;

    @JMap
    private String position;

    @JMap
    private String linkedInUrl;

    @JMap
    private CompanyDTO company;

    @JMap
    private Set<UserAuthDTO> users;

    public Set<UserAuthDTO> getUsers() {
        if (users == null) {
            users = new HashSet<>();
        }
        return users;
    }
}
