package com.sawoo.pipeline.api.service.base.event;

import com.sawoo.pipeline.api.model.BaseEntity;


public class BaseServiceBeforeInsertEvent<D, M extends BaseEntity> extends BaseServiceEvent<D, M> {

    public BaseServiceBeforeInsertEvent(D dto, M model) {
        super(dto, model);
    }
}
