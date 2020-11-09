package com.sawoo.pipeline.api.dto.prospect;

import com.googlecode.jmapper.annotations.JMap;
import com.sawoo.pipeline.api.dto.StatusDTO;
import com.sawoo.pipeline.api.model.common.Note;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class LeadDTO extends LeadBaseDTO {

    @JMap
    private Integer salutation;

    @JMap
    private String email;

    @JMap
    private String phoneNumber;

    @JMap
    private Note extraNotes;

    @JMap
    private Note companyComments;

    @JMap
    private StatusDTO status;

    @JMap
    private LocalDateTime created;

    @JMap
    private LocalDateTime updated;
}
