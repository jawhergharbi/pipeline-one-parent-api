package com.sawoo.pipeline.api.dto.lead;

import com.googlecode.jmapper.annotations.JMap;
import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.dto.BaseEntityDTO;
import com.sawoo.pipeline.api.dto.account.AccountLeadDTO;
import com.sawoo.pipeline.api.dto.person.PersonDTO;
import com.sawoo.pipeline.api.dto.person.PersonValid;
import com.sawoo.pipeline.api.model.common.Note;
import com.sawoo.pipeline.api.model.common.Status;
import lombok.*;
import org.springframework.cloud.gcp.data.datastore.core.mapping.Field;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
@Builder(toBuilder = true)
public class LeadDTO extends BaseEntityDTO {

    @JMap
    private String id;

    @JMap
    @PersonValid(message = ExceptionMessageConstants.PERSON_CROSS_FIELD_VALIDATION_ERROR)
    @NotNull(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_NULL_ERROR)
    private PersonDTO person;

    @JMap
    @Field(name = "linked_in_thread")
    private String linkedInThread;

    @JMap
    @Field(name = "lead_notes")
    private Note leadNotes;

    @JMap
    @Field(name = "company_notes")
    private Note companyNotes;

    @JMap
    private Status status;

    private AccountLeadDTO account;
}
