package com.sawoo.pipeline.api.integration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public  @interface MongoTestFile {
    /**
     * The class of objects stored in the MongoDB test file.
     * @return  The class of objects stored in the MongoDB test file.
     */
    Class<?> classType();

    /**
     * The name of the MongoDB JSON test file.
     * @return  The name of the MongoDB JSON test file.
     */
    String fileName();
}
