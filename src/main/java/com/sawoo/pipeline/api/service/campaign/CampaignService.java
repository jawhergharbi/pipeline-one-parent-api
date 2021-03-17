package com.sawoo.pipeline.api.service.campaign;

import com.sawoo.pipeline.api.dto.audit.VersionDTO;
import com.sawoo.pipeline.api.dto.campaign.CampaignDTO;
import com.sawoo.pipeline.api.repository.campaign.CampaignRepository;
import com.sawoo.pipeline.api.service.base.BaseProxyService;
import com.sawoo.pipeline.api.service.base.BaseService;

import java.util.List;

public interface CampaignService extends BaseService<CampaignDTO>, BaseProxyService<CampaignRepository, CampaignMapper>, CampaignAccountService {

    List<VersionDTO<CampaignDTO>> getVersions(String id);
}
