package com.wec.community.dao;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Repository
@Primary //优先级更高
public class AlphaDaoImpl2 implements AlphaDao{
    @Override
    public String select() {
        return "AlphaDaoImpl2";
    }
}
