package com.sawoo.pipeline.api.dto.lead;

import com.sawoo.pipeline.api.dto.interaction.InteractionDTO;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class LeadInteractionDTO extends InteractionDTO {

    String fullName;
}
