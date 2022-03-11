package com.wec.community;

import com.wec.community.util.SensitiveFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class SensitiveTests {
    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Test
    public void testSensitiveFilter(){
        String text = "这里面可以吸毒，可以嫖￥娼，可以￥赌￥博￥，还可以开票，哈哈哈哈";
        String filter = sensitiveFilter.filter(text);
        System.out.println(filter);
    }
}
