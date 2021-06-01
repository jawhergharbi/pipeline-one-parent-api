package com.sawoo.pipeline.api.dummy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class DummyServiceImpl implements DummyService {
    private final DummyRepository repository;

    @Override
    public List<DummyEntity> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<DummyEntity> findById(String id) {
        return repository.findById(id);
    }

    @Override
    public DummyEntity save(DummyEntity dummy) {
        if (dummy.getVersion() == null) dummy.setVersion(1);
        if (dummy.getId() == null) dummy.setId(UUID.randomUUID().toString());
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        dummy.setDateTime(now);
        return repository.save(dummy);
    }

    @Override
    public boolean delete(String id) {
        return repository
                .findById(id)
                .map( (dummy) -> {
                    repository.deleteById(id);
                    return true;
                }).orElse(false);
    }

    @Override
    public Optional<DummyEntity> update(String id, DummyEntity updatedDummy) {
        Optional<DummyEntity> dummyEntity = repository.findById(id);
        return dummyEntity.map( (dummy) -> {
            dummy.setVersion(dummy.getVersion() + 1);
            if (updatedDummy.getName() != null) dummy.setName(updatedDummy.getName());
            if (updatedDummy.getNumber() != null) dummy.setNumber(updatedDummy.getNumber());
            repository.save(dummy);
            return Optional.of(dummy);
        }).orElse(Optional.empty());
    }
}
