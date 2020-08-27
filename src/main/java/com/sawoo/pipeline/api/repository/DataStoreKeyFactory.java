package com.sawoo.pipeline.api.repository;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class DataStoreKeyFactory {

    private final Datastore datastore;

    private Map<String, KeyFactory> keyFactoryMap;


    public KeyFactory getKeyFactory(String kind) {
        if (keyFactoryMap == null) {
            keyFactoryMap = new HashMap<>();
        }
        KeyFactory keyFactory = keyFactoryMap.get(kind);
        if (keyFactory == null) {
            keyFactory = datastore.newKeyFactory().setKind(kind);
            keyFactoryMap.put(kind, keyFactory);
        }
        return keyFactory;
    }

    public Key allocatedId(String kind) {
        return datastore.allocateId(getKeyFactory(kind).newKey());
    }
}
