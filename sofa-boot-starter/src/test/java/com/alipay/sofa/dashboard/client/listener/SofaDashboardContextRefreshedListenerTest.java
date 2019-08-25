package com.alipay.sofa.dashboard.client.listener;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author chpengzh@foxmail.com
 * @date 2019-08-25 15:04
 */
@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = SofaDashboardContextRefreshedListenerTest.TestApplicationContext.class,
        webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class SofaDashboardContextRefreshedListenerTest {

    @Test
    public void contextRefreshedTest() {
        // A new context will be created, since then both
    }

    @SpringBootApplication(scanBasePackages = "no.such.package")
    public static class TestApplicationContext {
    }
}
