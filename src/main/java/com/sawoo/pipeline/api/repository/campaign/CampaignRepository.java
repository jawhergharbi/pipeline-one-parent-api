package com.sawoo.pipeline.api.repository.campaign;

import com.sawoo.pipeline.api.model.campaign.Campaign;
import com.sawoo.pipeline.api.model.campaign.CampaignStatus;
import com.sawoo.pipeline.api.repository.base.BaseMongoRepository;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@JaversSpringDataAuditable
public interface CampaignRepository extends BaseMongoRepository<Campaign> {

    List<Campaign> findByStatus(CampaignStatus status);

    List<Campaign> findByComponentId(String componentId);

    @Query(value="{ componentId : ?0}",
            fields="{ 'id' : 1, 'name': 1, 'description': 1, 'componentId': 1, 'status': 1, 'startDate': 1, 'endDate': 1, 'actualStartDate': 1, 'actualEndDate': 1, 'updated': 1, 'created': 1}")
    List<Campaign> findByComponentIdShort(String componentId);

    List<Campaign> findByComponentIdAndStatus(String componentId, CampaignStatus status);

    List<Campaign> findByComponentIdIn(Set<String> componentIds);

    @Query(value="{ componentId : {$in: ?0}}",
            fields="{ 'id' : 1, 'name': 1, 'description': 1, 'componentId': 1, 'status': 1, 'startDate': 1, 'endDate': 1, 'actualStartDate': 1, 'actualEndDate': 1, 'updated': 1, 'created': 1}")
    List<Campaign> findByComponentIdInShort(Set<String> componentIds);

    List<Campaign> findByComponentIdInAndStatus(Set<String> componentIds, CampaignStatus status);

    Optional<Campaign> findByComponentIdAndName(String componentId, String name);
}
