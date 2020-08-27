package com.sawoo.pipeline.api.dto.lead;

import com.sawoo.pipeline.api.dto.client.ClientBaseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class LeadMainDTO extends LeadBasicDTO {

    private LeadInteractionDTO last;

    private LeadInteractionDTO next;

    private ClientBaseDTO client;
}
