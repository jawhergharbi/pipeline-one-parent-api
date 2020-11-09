package com.sawoo.pipeline.api.service.base;

public interface BaseServiceEventListener<D, M> {
    void onBeforeCreate(D dto, M entity);

    void onBeforeUpdate(D dto, M entity);
}
