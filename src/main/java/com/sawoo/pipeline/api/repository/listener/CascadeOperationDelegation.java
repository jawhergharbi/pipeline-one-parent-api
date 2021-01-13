package com.sawoo.pipeline.api.repository.listener;

import com.sawoo.pipeline.api.model.BaseEntity;

import java.util.function.Consumer;

public interface CascadeOperationDelegation<C extends BaseEntity> {
    void onSave(C child, Consumer<C> parentFunction);
}
