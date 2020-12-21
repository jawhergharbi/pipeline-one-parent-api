package com.sawoo.pipeline.api.repository.interaction;

import com.google.cloud.datastore.Key;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.prospect.LeadInteractionOld;
import com.sawoo.pipeline.api.repository.DataStoreKeyFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LeadInteractionRepositoryWrapperImpl implements LeadInteractionRepositoryWrapper {

    private final DataStoreKeyFactory datastoreKeyFactory;
    private final LeadInteractionRepositoryOld repository;


    @Override
    public LeadInteractionOld save(Long leadId, LeadInteractionOld interaction) {
        log.debug("Save interaction for lead id [{}]. Interaction: [{}]", leadId, interaction);

        Key parentKey = datastoreKeyFactory.getKeyFactory(DBConstants.LEAD_DOCUMENT).newKey(leadId);
        Key leadKey = Key.newBuilder(
                parentKey,
                DBConstants.LEAD_ACTION_DOCUMENT,
                datastoreKeyFactory.allocatedId(DBConstants.LEAD_ACTION_DOCUMENT).getId()).build();
        interaction.setKey(leadKey);

        log.debug("Key object created [{}]", leadKey.toString());
        return repository.save(interaction);
    }

    @Override
    public LeadInteractionOld save(LeadInteractionOld interaction) {
        log.debug("Save interaction: [{}]", interaction);
        return repository.save(interaction);
    }

    @Override
    public Optional<LeadInteractionOld> findById(Long leadId, Long interactionId) {
        log.debug("Retrieve interaction for lead id [{}] and lead interaction id[{}]", leadId, interactionId);

        Key parentKey = datastoreKeyFactory.getKeyFactory(DBConstants.LEAD_DOCUMENT).newKey(leadId);
        Key leadKey = Key.newBuilder(parentKey,  DBConstants.LEAD_ACTION_DOCUMENT, interactionId).build();

        return repository.findById(leadKey);
    }

    @Override
    public Optional<LeadInteractionOld> deleteById(Long leadId, Long interactionId) {
        log.debug("Delete interaction for lead id [{}] and interaction id[{}]", leadId, interactionId);

        return findById(leadId, interactionId)
                .map( (interaction) -> {
                    repository.delete(interaction);

                    log.debug("Interaction id [{}] for lead id [{}] has been successfully deleted", interactionId, leadId);
                    return interaction;
                });
    }
}
