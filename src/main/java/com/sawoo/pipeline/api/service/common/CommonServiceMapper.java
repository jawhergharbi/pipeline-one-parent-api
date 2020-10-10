package com.sawoo.pipeline.api.service.common;

import com.googlecode.jmapper.JMapper;
import com.sawoo.pipeline.api.dto.client.ClientBaseDTO;
import com.sawoo.pipeline.api.dto.client.ClientBasicDTO;
import com.sawoo.pipeline.api.dto.company.CompanyDTO;
import com.sawoo.pipeline.api.dto.interaction.InteractionDTO;
import com.sawoo.pipeline.api.dto.lead.LeadBaseDTO;
import com.sawoo.pipeline.api.dto.lead.LeadDTO;
import com.sawoo.pipeline.api.dto.lead.LeadInteractionDTO;
import com.sawoo.pipeline.api.dto.lead.LeadMainDTO;
import com.sawoo.pipeline.api.dto.user.UserDTO;
import com.sawoo.pipeline.api.model.Company;
import com.sawoo.pipeline.api.model.User;
import com.sawoo.pipeline.api.model.client.Client;
import com.sawoo.pipeline.api.model.lead.Lead;
import com.sawoo.pipeline.api.model.lead.LeadInteraction;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Getter
@RequiredArgsConstructor
@Component
public class CommonServiceMapper {

    private final JMapper<LeadDTO, Lead> leadDomainToDTOMapper = new JMapper<>(LeadDTO.class, Lead.class);
    private final JMapper<LeadBaseDTO, Lead> leadDomainToDTOBaseMapper = new JMapper<>(LeadBaseDTO.class, Lead.class);
    private final JMapper<LeadMainDTO, Lead> leadDomainToDTOMainMapper = new JMapper<>(LeadMainDTO.class, Lead.class);

    private final JMapper<Lead, LeadDTO> leadDTOToDomainMapper = new JMapper<>(Lead.class, LeadDTO.class);
    private final JMapper<LeadInteractionDTO, LeadInteraction> leadInteractionDomainToDTOMapper = new JMapper<>(LeadInteractionDTO.class, LeadInteraction.class);
    private final JMapper<InteractionDTO, LeadInteraction> interactionDomainToDTOMapper = new JMapper<>(InteractionDTO.class, LeadInteraction.class);

    private final JMapper<ClientBasicDTO, Client> clientDomainToDTOBasicMapper = new JMapper<>(ClientBasicDTO.class, Client.class);
    private final JMapper<Client, ClientBasicDTO> clientDTOToDomainMapper = new JMapper<>(Client.class, ClientBasicDTO.class);
    private final JMapper<ClientBaseDTO, Client> clientDomainToDTOBaseMapper = new JMapper<>(ClientBaseDTO.class, Client.class);

    private final JMapper<CompanyDTO, Company> companyDomainToDTOMapper = new JMapper<>(CompanyDTO.class, Company.class);
    private final JMapper<Company, CompanyDTO> companyDTOToDomainMapper = new JMapper<>(Company.class, CompanyDTO.class);

    private final JMapper<UserDTO, User> userDomainToDTOMapper = new JMapper<>(UserDTO.class, User.class);
    private final JMapper<User, UserDTO> userDTOToDomainMapper = new JMapper<>(User.class, UserDTO.class);

}
