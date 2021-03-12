package com.sawoo.pipeline.api.service.base;

import com.sawoo.pipeline.api.model.BaseEntity;
import lombok.Data;
import org.springframework.core.ResolvableType;
import org.springframework.core.ResolvableTypeProvider;

@Data
public class BeforeInsertEvent <D, M extends BaseEntity> implements ResolvableTypeProvider {

    private D dto;
    private M model;

    public BeforeInsertEvent(D dto, M model) {
        this.dto = dto;
        this.model = model;
    }

    @Override
    public ResolvableType getResolvableType() {
        return ResolvableType.forClassWithGenerics(getClass(),
                ResolvableType.forInstance(dto), ResolvableType.forInstance(model));
    }
}
