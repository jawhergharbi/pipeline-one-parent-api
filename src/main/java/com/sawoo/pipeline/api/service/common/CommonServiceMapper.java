package com.sawoo.pipeline.api.service.common;

import com.googlecode.jmapper.JMapper;
import com.sawoo.pipeline.api.dto.client.ClientBaseDTO;
import com.sawoo.pipeline.api.dto.client.ClientBasicDTO;
import com.sawoo.pipeline.api.dto.interaction.InteractionDTO;
import com.sawoo.pipeline.api.dto.prospect.LeadBaseDTO;
import com.sawoo.pipeline.api.dto.prospect.LeadDTOOld;
import com.sawoo.pipeline.api.dto.prospect.LeadInteractionDTOOld;
import com.sawoo.pipeline.api.dto.prospect.LeadMainDTO;
import com.sawoo.pipeline.api.dto.user.UserDTOOld;
import com.sawoo.pipeline.api.model.UserOld;
import com.sawoo.pipeline.api.model.client.Client;
import com.sawoo.pipeline.api.model.prospect.LeadInteractionOld;
import com.sawoo.pipeline.api.model.prospect.LeadOld;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Getter
@RequiredArgsConstructor
@Component
public class CommonServiceMapper {

    private final JMapper<LeadDTOOld, LeadOld> leadDomainToDTOMapper = new JMapper<>(LeadDTOOld.class, LeadOld.class);
    private final JMapper<LeadBaseDTO, LeadOld> leadDomainToDTOBaseMapper = new JMapper<>(LeadBaseDTO.class, LeadOld.class);
    private final JMapper<LeadMainDTO, LeadOld> leadDomainToDTOMainMapper = new JMapper<>(LeadMainDTO.class, LeadOld.class);

    private final JMapper<LeadOld, LeadDTOOld> leadDTOToDomainMapper = new JMapper<>(LeadOld.class, LeadDTOOld.class);
    private final JMapper<LeadInteractionDTOOld, LeadInteractionOld> leadInteractionDomainToDTOMapper = new JMapper<>(LeadInteractionDTOOld.class, LeadInteractionOld.class);
    private final JMapper<InteractionDTO, LeadInteractionOld> interactionDomainToDTOMapper = new JMapper<>(InteractionDTO.class, LeadInteractionOld.class);

    private final JMapper<ClientBasicDTO, Client> clientDomainToDTOBasicMapper = new JMapper<>(ClientBasicDTO.class, Client.class);
    private final JMapper<Client, ClientBasicDTO> clientDTOToDomainMapper = new JMapper<>(Client.class, ClientBasicDTO.class);
    private final JMapper<ClientBaseDTO, Client> clientDomainToDTOBaseMapper = new JMapper<>(ClientBaseDTO.class, Client.class);

    private final JMapper<UserDTOOld, UserOld> userDomainToDTOMapper = new JMapper<>(UserDTOOld.class, UserOld.class);
    private final JMapper<UserOld, UserDTOOld> userDTOToDomainMapper = new JMapper<>(UserOld.class, UserDTOOld.class);

}
