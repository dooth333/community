package com.wec.community;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;


import javax.annotation.PostConstruct;

@SpringBootApplication
public class CommunityApplication  extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(CommunityApplication.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(CommunityApplication.class, args);
	}

	@PostConstruct //bean的初始化方法
	public void init(){
		//解决netty启动冲突的问题
		//可以看 Netty4Utils.setAvailableProcessors
		System.setProperty("es.set.netty.runtime.available.processors","false");
	}

}
