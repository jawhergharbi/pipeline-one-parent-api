package com.sawoo.pipeline.api.integration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation that interacts with the MongoSpringExtension that provides information about the test MongoDB
 * JSON file for this method as well as the collection name and type of objects stored in the test file.
 */

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface MongoCleanUp {

    /**
     * The name of the MongoDB collections hosting the test objects to be dropped from the database.
     * @return  The name of the MongoDB collections hosting the test objects.
     */
    String[] collectionNames();
}
