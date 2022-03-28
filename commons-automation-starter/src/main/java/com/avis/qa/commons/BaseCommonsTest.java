package com.avis.qa.commons;

import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

@Slf4j
public class BaseCommonsTest {

    @BeforeSuite
    public void setUp() {
        log.info("setup");
    }

    @AfterSuite
    public void tearDown() {
        log.info("setup");
    }
}
