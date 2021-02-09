package com.sawoo.pipeline.api.dto.lead;

import com.sawoo.pipeline.api.dto.account.AccountLeadDTO;
import com.sawoo.pipeline.api.dto.interaction.InteractionAssigneeDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class LeadInteractionDTO extends InteractionAssigneeDTO {

    private AccountLeadDTO account;

    private LeadInteractionLeadDTO lead;
}
