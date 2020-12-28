package com.sawoo.pipeline.api.dto.prospect;

import com.sawoo.pipeline.api.dto.client.ClientBaseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class LeadMainDTOOld extends LeadDTOOld {

    private ClientBaseDTO client;
}
