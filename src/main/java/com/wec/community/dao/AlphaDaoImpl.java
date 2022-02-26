package com.wec.community.dao;

import org.springframework.stereotype.Repository;

@Repository("alphaHibernate")//括号中自定义bean的名字
public class AlphaDaoImpl implements AlphaDao{
    @Override
    public String select() {
        return "Hibernate";
    }
}
