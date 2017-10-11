package com.taotao.mq;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

public class TestSpringActiveMQ {

	@Test 
	public void testQueneProducer() throws Exception {
		// 初始化spring容器
		ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-activemq.xml");
		// 从容器中获取jmsTemplate对象
		JmsTemplate jmsTemplate = ctx.getBean(JmsTemplate.class);
		// 从容器中获取Desidition对象
		Topic topic = ctx.getBean(Topic.class);
		// 使用jmsTemplate发送消息
		// 第一个参数:指定发送的目的地
		// 第二个参数:消息的构造器对象，就是创建一个消息
		jmsTemplate.send(topic, new MessageCreator() {
			
			@Override
			public Message createMessage(Session session) throws JMSException {
				TextMessage message = session.createTextMessage("使用Spring和ActiveMQ整合发送queue");
				return message;
			}
		});
	}
}
