package com.sawoo.pipeline.api.dummy;

import java.util.List;
import java.util.Optional;

public interface DummyService {

    List<DummyEntity> findAll();
    Optional<DummyEntity> findById(String id);
    DummyEntity save(DummyEntity dummy);
    boolean delete(String id);
    Optional<DummyEntity> update(String id, DummyEntity dummy);

}
