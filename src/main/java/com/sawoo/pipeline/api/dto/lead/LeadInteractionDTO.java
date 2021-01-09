package com.sawoo.pipeline.api.dto.lead;

import com.sawoo.pipeline.api.dto.account.AccountLeadDTO;
import com.sawoo.pipeline.api.dto.interaction.InteractionDTO;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class LeadInteractionDTO extends InteractionDTO {

    private AccountLeadDTO account;

    private LeadInteractionLeadDTO lead;
}
