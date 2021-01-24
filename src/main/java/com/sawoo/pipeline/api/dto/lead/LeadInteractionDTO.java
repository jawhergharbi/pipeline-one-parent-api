package com.sawoo.pipeline.api.dto.lead;

import com.sawoo.pipeline.api.dto.account.AccountLeadDTO;
import com.sawoo.pipeline.api.dto.interaction.InteractionDTO;
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
public class LeadInteractionDTO extends InteractionDTO {

    private AccountLeadDTO account;

    private LeadInteractionLeadDTO lead;
}
