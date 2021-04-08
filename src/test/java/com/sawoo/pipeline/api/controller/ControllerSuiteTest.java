package com.sawoo.pipeline.api.controller;

import org.junit.platform.runner.JUnitPlatform;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.runner.RunWith;

@RunWith(JUnitPlatform.class)
@SelectPackages({
        "com.sawoo.pipeline.api.controller.account",
        "com.sawoo.pipeline.api.controller.company",
        "com.sawoo.pipeline.api.controller.person",
        "com.sawoo.pipeline.api.controller.todo",
        "com.sawoo.pipeline.api.controller.user",
        "com.sawoo.pipeline.api.controller.campaign",
        "com.sawoo.pipeline.api.controller.sequence",
        "com.sawoo.pipeline.api.controller.prospect",
        "com.sawoo.pipeline.api.controller.security"})
public class ControllerSuiteTest {
}
