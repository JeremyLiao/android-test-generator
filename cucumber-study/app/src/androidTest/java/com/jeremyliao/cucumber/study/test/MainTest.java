package com.jeremyliao.cucumber.study.test;


import junit.framework.TestCase;

import cucumber.api.CucumberOptions;

/**
 * Created by liaohailiang on 2019/1/10.
 */
@CucumberOptions(
        features = "features",
        glue = "com.jeremyliao.bddespresso.steps")
public class MainTest extends TestCase {
}
