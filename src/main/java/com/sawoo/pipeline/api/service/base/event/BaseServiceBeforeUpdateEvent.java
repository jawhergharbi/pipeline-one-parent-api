package com.sawoo.pipeline.api.service.base.event;

import com.sawoo.pipeline.api.model.BaseEntity;

public class BaseServiceBeforeUpdateEvent<D, M extends BaseEntity> extends BaseServiceEvent<D, M> {

    public BaseServiceBeforeUpdateEvent(D dto, M model) {
        super(dto, model);
    }
}
