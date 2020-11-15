package com.sawoo.pipeline.api.service.base;

public interface BaseServiceEventListener<D, M> {

    void onBeforeInsert(D dto, M entity);

    void onBeforeSave(D dto, M entity);

    void onBeforeUpdate(D dto, M entity);
}
