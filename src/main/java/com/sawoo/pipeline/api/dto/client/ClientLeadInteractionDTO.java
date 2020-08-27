package com.sawoo.pipeline.api.dto.client;

import com.sawoo.pipeline.api.dto.lead.LeadInteractionDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class ClientLeadInteractionDTO {

    private String leadName;
    private LeadInteractionDTO interaction;
}
