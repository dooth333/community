package com.wec.community;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
/**
 * 日志测试类
 */
public class LoggerTests {

    private static final Logger logger = LoggerFactory.getLogger(LoggerTests.class);


    @Test
    public void testLogger(){
        System.out.println(logger.getName());
        logger.debug("debugLog");
        logger.info("infoLog");
        logger.warn("warnLog");
        logger.error("errorLog");
    }
}
