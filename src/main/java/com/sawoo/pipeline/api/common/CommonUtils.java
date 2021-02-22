package com.sawoo.pipeline.api.common;

import java.util.Collection;

public class CommonUtils {

    public static boolean isEmptyOrNull(Collection<?> collection) {
        return (collection == null || collection.isEmpty());
    }

    public static boolean isNotEmptyNorNull(Collection<?> collection) {
        return !isEmptyOrNull(collection);
    }
}
