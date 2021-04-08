package com.sawoo.pipeline.api.common.validation;

import java.io.Serializable;

public interface IEnum<T extends Serializable> {
    T getValue();
}
