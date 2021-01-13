package com.sawoo.pipeline.api.controller.base;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sawoo.pipeline.api.mock.MockFactory;
import com.sawoo.pipeline.api.model.BaseEntity;
import com.sawoo.pipeline.api.service.base.BaseService;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public abstract class BaseLightControllerTest<D, M extends BaseEntity, S extends BaseService<D>, F extends MockFactory<D, M>> {

    private F mockFactory;
    private String resourceURI;
    private String entityType;
    private S service;

    public BaseLightControllerTest(F mockFactory, String resourceURI, String entityType, S service) {
        this.mockFactory = mockFactory;
        this.resourceURI = resourceURI;
        this.entityType = entityType;
        this.service = service;
    }

    protected static String asJsonString(final Object obj) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            mapper.disable(MapperFeature.USE_ANNOTATIONS);
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
