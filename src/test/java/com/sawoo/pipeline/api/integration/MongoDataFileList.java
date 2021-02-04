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
public @interface MongoDataFileList {

    /**
     * The name of the MongoDB JSON test file.
     * @return  The name of the MongoDB JSON test file.
     */
    MongoTestFile[] files();

    /**
     * The name of the MongoDB collection hosting the test objects.
     * @return  The name of the MongoDB collection hosting the test objects.
     */
    String[] collectionNames();
}
