package com.sawoo.pipeline.api.config;

import com.sawoo.pipeline.api.model.DataStoreConverter;
import org.springframework.cloud.gcp.data.datastore.core.convert.DatastoreCustomConversions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.Arrays;

@Configuration
@Profile(value = {"dev-local", "dev", "test", "prod"})
public class DataStoreConfig {

    @Bean
    public DatastoreCustomConversions datastoreCustomConversions() {
        return new DatastoreCustomConversions(
                Arrays.asList(DataStoreConverter.LIST_SET_CONVERTER));
    }
}
