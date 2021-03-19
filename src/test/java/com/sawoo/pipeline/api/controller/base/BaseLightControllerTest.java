package com.sawoo.pipeline.api.controller.base;

import com.sawoo.pipeline.api.mock.MockFactory;
import com.sawoo.pipeline.api.model.BaseEntity;
import com.sawoo.pipeline.api.service.base.BaseService;
import com.sawoo.pipeline.api.utils.JacksonObjectMapperUtils;
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
        return JacksonObjectMapperUtils.asJsonString(obj);
    }
}
