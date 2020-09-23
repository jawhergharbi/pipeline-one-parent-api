package com.sawoo.pipeline.api.dto.client;

import com.sawoo.pipeline.api.dto.lead.LeadInteractionDTO;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class ClientLeadInteractionDTO {

    private String leadName;
    private LeadInteractionDTO interaction;
}
