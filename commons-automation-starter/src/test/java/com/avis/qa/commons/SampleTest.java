package com.avis.qa.commons;

import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

@Slf4j
public class SampleTest extends BaseCommonsTest {

    @Test
    public void test() {
        log.info("Sample test");
    }
}
