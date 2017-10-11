package com.taotao.mq;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestActiveMQ {

	
	@Test 
	public void testActivemq() throws Exception {
		// 初始化spring容器
		ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-activemq.xml");
		// 一旦spring容器初始化后程序就结束了，但我们不能让他结束，得让他等着
		// 你什么时候发消息，我就什么时候接受
		System.in.read();
	}
}
