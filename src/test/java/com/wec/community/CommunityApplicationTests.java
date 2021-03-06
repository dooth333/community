package com.wec.community;


import com.wec.community.dao.AlphaDao;
import com.wec.community.dao.DiscussPostMapper;
import com.wec.community.entity.DiscussPost;
import com.wec.community.service.AlphaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;

import java.text.SimpleDateFormat;
import java.util.Date;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
class CommunityApplicationTests {
		//implements ApplicationContextAware {
	/*private ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
	@Test
	public void testApplicationContext(){
		System.out.println(applicationContext);
		AlphaDao alphaDao = applicationContext.getBean(AlphaDao.class);
		System.out.println(alphaDao.select());
		alphaDao = applicationContext.getBean("alphaHibernate",AlphaDao.class);
		System.out.println(alphaDao.select());
	}
	@Test
	public void testBeanManagement(){
		AlphaService alphaService = applicationContext.getBean(AlphaService.class);
		System.out.println(alphaService);
		alphaService = applicationContext.getBean(AlphaService.class);
		System.out.println(alphaService);
	}
	@Test
	public void testBeanConfig(){
		SimpleDateFormat simpleDateFormat = applicationContext.getBean(SimpleDateFormat.class);
		System.out.println(simpleDateFormat.format(new Date()));
	}

	@Autowired
	@Qualifier("alphaDaoImpl2")
	private AlphaDao alphaDao;

	@Autowired
	private AlphaService alphaService;
	@Test
	public void testDI(){
		System.out.println(alphaDao);
		System.out.println(alphaService);
	}

	@Autowired
	private DiscussPostMapper discussPostMapper;
	@Test
	public void insertPostText(){
		DiscussPost discussPost = new DiscussPost(001,"????????????????????????","???????????????????????????",0,0,new Date(),0,0);

		discussPostMapper.insertDiscussPost(discussPost);
	}*/

}
