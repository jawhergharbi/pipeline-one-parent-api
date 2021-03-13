package com.sawoo.pipeline.api.service.base.event;

import com.sawoo.pipeline.api.model.BaseEntity;

public class BaseServiceBeforeSaveEvent<D, M extends BaseEntity> extends BaseServiceEvent<D, M> {

    public BaseServiceBeforeSaveEvent(D dto, M model) {
        super(dto, model);
    }
}
