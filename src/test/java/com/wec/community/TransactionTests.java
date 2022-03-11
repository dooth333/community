package com.wec.community;

import com.wec.community.service.AlphaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class TransactionTests {
    @Autowired
    private AlphaService alphaService;
    @Test
    public void TransactionTest1(){
        Object o = alphaService.save1();
        System.out.println(o);
    }

    @Test
    public void TransactionTest2(){
        Object o = alphaService.save2();
        System.out.println(o);
    }


}
