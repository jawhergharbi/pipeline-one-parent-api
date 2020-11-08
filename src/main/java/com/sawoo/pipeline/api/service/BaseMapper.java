package com.sawoo.pipeline.api.service;

import com.googlecode.jmapper.JMapper;

public interface BaseMapper<D, M> {

    JMapper<M, D> getMapperIn();
    JMapper<D, M> getMapperOut();
}
