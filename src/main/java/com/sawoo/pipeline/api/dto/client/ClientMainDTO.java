package com.sawoo.pipeline.api.dto.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ClientMainDTO extends ClientBasicDTO {

    private ClientLeadInteractionDTO nextInteraction;
}
