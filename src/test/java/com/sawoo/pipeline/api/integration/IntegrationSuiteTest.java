package com.sawoo.pipeline.api.integration;

import org.junit.platform.runner.JUnitPlatform;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.runner.RunWith;

@RunWith(JUnitPlatform.class)
@SelectPackages({
        "com.sawoo.pipeline.api.integration.account",
        "com.sawoo.pipeline.api.integration.company",
        "com.sawoo.pipeline.api.integration.person",
        "com.sawoo.pipeline.api.integration.todo",
        "com.sawoo.pipeline.api.integration.user",
        "com.sawoo.pipeline.api.integration.sequence",
        "com.sawoo.pipeline.api.integration.prospect"})
public class IntegrationSuiteTest {
}
