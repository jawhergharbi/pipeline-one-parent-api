package com.sawoo.pipeline.api.dto.account;

import com.googlecode.jmapper.annotations.JMap;
import com.sawoo.pipeline.api.dto.BaseEntityDTO;
import com.sawoo.pipeline.api.dto.company.CompanyDTO;
import lombok.*;

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
}
