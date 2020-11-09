package com.sawoo.pipeline.api.dto.interaction;

import com.sawoo.pipeline.api.dto.client.ClientBaseDTO;
import com.sawoo.pipeline.api.dto.prospect.LeadBaseDTO;
import com.sawoo.pipeline.api.dto.prospect.LeadInteractionDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class InteractionDTO extends LeadInteractionDTO {

    private ClientBaseDTO client;

    private LeadBaseDTO lead;
}
