package com.sawoo.pipeline.api.repository.campaign;

import com.sawoo.pipeline.api.model.campaign.Campaign;
import com.sawoo.pipeline.api.model.campaign.CampaignStatus;
import com.sawoo.pipeline.api.repository.base.BaseMongoRepository;
import org.javers.spring.annotation.JaversSpringDataAuditable;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@JaversSpringDataAuditable
public interface CampaignRepository extends BaseMongoRepository<Campaign> {

    List<Campaign> findByStatus(CampaignStatus status);

    List<Campaign> findByComponentId(String componentId);

    List<Campaign> findByComponentIdAndStatus(String componentId, CampaignStatus status);

    List<Campaign> findByComponentIdIn(Set<String> componentIds);

    List<Campaign> findByComponentIdInAndStatus(Set<String> componentIds, CampaignStatus status);

    Optional<Campaign> findByComponentIdAndName(String componentId, String name);
}
