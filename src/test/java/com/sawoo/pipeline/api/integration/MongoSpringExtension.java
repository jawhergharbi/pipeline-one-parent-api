package com.sawoo.pipeline.api.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.extension.*;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

public class MongoSpringExtension implements BeforeEachCallback, AfterEachCallback, BeforeAllCallback, AfterAllCallback {

    private static final Path JSON_PATH = Paths.get("src", "test", "resources", "test-data", "integration");

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        context.getTestMethod().ifPresent(method -> {
            // Load the MongoDataFile annotation value from the test method
            MongoDataFile mongoDataFile = method.getAnnotation(MongoDataFile.class);

            // Load the MongoTemplate that we can use to drop the test collection
            dropCollection(context, mongoDataFile);
        });
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        context.getTestMethod().ifPresent(method -> {
            // Load test file from the annotation
            MongoDataFile mongoDataFile = method.getAnnotation(MongoDataFile.class);

            // Load the MongoTemplate that we can use to import our data
            insertCollection(context, mongoDataFile);
        });
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        context.getTestClass().ifPresent(clazz -> {
            // Load test file from the annotation
            MongoDataFile mongoDataFile = clazz.getAnnotation(MongoDataFile.class);

            // Load the MongoTemplate that we can use to import our data
            dropCollection(context, mongoDataFile);
        });
    }

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        context.getTestClass().ifPresent(clazz -> {
            // Load test file from the annotation
            MongoDataFile mongoDataFile = clazz.getAnnotation(MongoDataFile.class);

            // Load the MongoTemplate that we can use to import our data
            insertCollection(context, mongoDataFile);
        });
    }

    /**
     * Helper method that uses reflection to invoke the getMongoTemplate() method on the test instance.
     * @param context The ExtensionContext, which provides access to the test instance.
     * @return  An optional MongoTemplate, if it exists.
     */
    private Optional<MongoTemplate> getMongoTemplate(ExtensionContext context) {
        Optional<Class<?>> clazz = context.getTestClass();
        if (clazz.isPresent()) {
            Class<?> c = clazz.get();
            try {
                // Find the getMongoTemplate method on the test class
                Method method = c.getMethod("getMongoTemplate",  null);

                // Invoke the getMongoTemplate method on the test class
                Optional<Object> testInstance = context.getTestInstance();
                if (testInstance.isPresent()) {
                    return Optional.of((MongoTemplate)method.invoke(testInstance.get(), null));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return Optional.empty();
    }

    private void dropCollection(ExtensionContext context, MongoDataFile mongoDataFile) {
        if (mongoDataFile != null) {
            Optional<MongoTemplate> mongoTemplate = getMongoTemplate(context);
            mongoTemplate.ifPresent(t -> t.dropCollection(mongoDataFile.collectionName()));
        }
    }

    private void insertCollection(ExtensionContext context, MongoDataFile mongoDataFile) {
        if (mongoDataFile != null) {
            getMongoTemplate(context).ifPresent(mongoTemplate -> {
                try {
                    // Use Jackson's ObjectMapper to load a list of objects from the JSON file
                    ObjectMapper mapper = new ObjectMapper();
                    mapper.registerModule(new JavaTimeModule());
                    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
                    List<?> objects = mapper.readValue(
                            JSON_PATH.resolve(mongoDataFile.value()).toFile(),
                            mapper.getTypeFactory().constructCollectionType(
                                    List.class, mongoDataFile.classType()));

                    // Save each object into MongoDB
                    mongoTemplate.insertAll(objects);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            });
        }
    }
}
