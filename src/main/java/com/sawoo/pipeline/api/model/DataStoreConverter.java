package com.sawoo.pipeline.api.model;


import org.springframework.core.convert.converter.Converter;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DataStoreConverter {
    public static final Converter<List<?>, Set<?>> LIST_SET_CONVERTER =
            new Converter<List<?>, Set<?>>() {
                @Override
                public Set<?> convert(List<?> source) {
                    return source.stream().collect(Collectors.toSet());
                }
            };
}
