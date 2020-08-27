package com.sawoo.pipeline.api.dummy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@Slf4j
@RequiredArgsConstructor
public class DummyServiceImpl implements DummyService {
    private final DummyRepository dummyRepository;

    @Override
    public List<DummyEntity> findAll() {
        return StreamSupport
                .stream( dummyRepository.findAll().spliterator(), false )
                .collect(Collectors.toList());
    }

    @Override
    public Optional<DummyEntity> findById(String id) {
        return dummyRepository.findById(id);
    }

    @Override
    public DummyEntity save(DummyEntity dummy) {
        if (dummy.getVersion() == null) dummy.setVersion(1);
        if (dummy.getId() == null) dummy.setId(UUID.randomUUID().toString());
        return dummyRepository.save(dummy);
    }

    @Override
    public boolean delete(String id) {
        return dummyRepository
                .findById(id)
                .map( (dummy) -> {
                    dummyRepository.deleteById(id);
                    return true;
                }).orElse(false);
    }

    @Override
    public Optional<DummyEntity> update(String id, DummyEntity updatedDummy) {
        Optional<DummyEntity> dummyEntity = dummyRepository.findById(id);
        return dummyEntity.map( (dummy) -> {
            dummy.setVersion(dummy.getVersion() + 1);
            if (updatedDummy.getName() != null) dummy.setName(updatedDummy.getName());
            if (updatedDummy.getNumber() != null) dummy.setNumber(updatedDummy.getNumber());
            dummyRepository.save(dummy);
            return Optional.of(dummy);
        }).orElse(Optional.empty());
    }
}
