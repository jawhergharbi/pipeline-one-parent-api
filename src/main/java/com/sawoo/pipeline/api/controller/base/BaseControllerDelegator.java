package com.sawoo.pipeline.api.controller.base;

import com.sawoo.pipeline.api.service.base.BaseService;

public abstract class BaseControllerDelegator<D, S extends BaseService<D>> implements ControllerDelegation<D> {

    private S service;
}
