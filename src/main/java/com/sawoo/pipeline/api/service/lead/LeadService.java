package com.sawoo.pipeline.api.service.lead;

import com.sawoo.pipeline.api.dto.lead.LeadDTO;
import com.sawoo.pipeline.api.dto.lead.LeadInteractionDTO;
import com.sawoo.pipeline.api.repository.lead.LeadRepository;
import com.sawoo.pipeline.api.service.base.BaseProxyService;
import com.sawoo.pipeline.api.service.base.BaseService;

public interface LeadService extends BaseService<LeadDTO>, BaseProxyService<LeadRepository, LeadMapper>, LeadReportService, LeadInteractionService {

    LeadInteractionDTO updateInteraction(String leadId, LeadInteractionDTO interaction);

}
