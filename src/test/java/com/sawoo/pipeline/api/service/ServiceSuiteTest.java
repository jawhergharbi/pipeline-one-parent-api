package com.sawoo.pipeline.api.service;

import org.junit.platform.runner.JUnitPlatform;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.runner.RunWith;

@RunWith(JUnitPlatform.class)
@SelectPackages({
        "com.sawoo.pipeline.api.service.account",
        "com.sawoo.pipeline.api.service.company",
        "com.sawoo.pipeline.api.service.person",
        "com.sawoo.pipeline.api.service.todo",
        "com.sawoo.pipeline.api.service.user",
        "com.sawoo.pipeline.api.service.campaign",
        "com.sawoo.pipeline.api.service.sequence",
        "com.sawoo.pipeline.api.service.prospect"})
public class ServiceSuiteTest {
}
