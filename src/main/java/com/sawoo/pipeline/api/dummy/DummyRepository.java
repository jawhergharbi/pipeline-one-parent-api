package com.sawoo.pipeline.api.dummy;

import org.springframework.cloud.gcp.data.datastore.repository.DatastoreRepository;

public interface DummyRepository extends DatastoreRepository<DummyEntity, String> {
}
