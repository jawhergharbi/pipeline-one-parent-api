package com.sawoo.pipeline.api.repository;

import org.junit.platform.runner.JUnitPlatform;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.runner.RunWith;

@RunWith(JUnitPlatform.class)
@SelectPackages({
        "com.sawoo.pipeline.api.repository.account",
        "com.sawoo.pipeline.api.repository.company",
        "com.sawoo.pipeline.api.repository.person",
        "com.sawoo.pipeline.api.repository.todo",
        "com.sawoo.pipeline.api.repository.user",
        "com.sawoo.pipeline.api.repository.campaign",
        "com.sawoo.pipeline.api.repository.sequence",
        "com.sawoo.pipeline.api.repository.prospect"})
public class RepositorySuiteTest {
}
