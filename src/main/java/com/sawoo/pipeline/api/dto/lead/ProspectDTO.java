package com.sawoo.pipeline.api.dto.lead;

import com.googlecode.jmapper.annotations.JMap;
import lombok.*;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ProspectDTO extends ProspectBaseDTO {

    @JMap
    private Integer salutation;

    @JMap
    private String email;

    @JMap
    private String phoneNumber;

    @JMap
    private LocalDateTime created;

    @JMap
    private LocalDateTime updated;
}
