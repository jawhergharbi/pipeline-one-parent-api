package com.sawoo.pipeline.api;

import com.github.cloudyrock.spring.v5.EnableMongock;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableMongock
@SpringBootApplication
public class PipelineApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(PipelineApiApplication.class, args);
    }

}
