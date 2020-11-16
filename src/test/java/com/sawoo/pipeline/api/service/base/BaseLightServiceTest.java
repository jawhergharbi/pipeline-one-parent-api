package com.sawoo.pipeline.api.service.base;

import com.sawoo.pipeline.api.mock.MockFactory;
import com.sawoo.pipeline.api.model.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.repository.MongoRepository;

@NoArgsConstructor
@Getter
public abstract class BaseLightServiceTest<D, M extends BaseEntity, R extends MongoRepository<M, String>, S extends BaseService<D>, F extends MockFactory<D, M>> {

    @Setter
    private R repository;
    private F mockFactory;
    private String entityType;
    private S service;

    public BaseLightServiceTest(F mockFactory, String entityType, S service) {
        this.mockFactory = mockFactory;
        this.entityType = entityType;
        this.service = service;
    }
}
