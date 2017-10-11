package com.taotao.search.listener;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

/**
 * 
* @ClassName: MyMessageListener
* @Description: 接收ActiveMQ队列消息的监听器
* @author ruwenbo
* @date 2017年9月27日 下午10:38:29
*
 */
public class MyMessageListener implements MessageListener {

	
	// onMessage 是一个事件，当消息到达的时候，会调用这个方法
	@Override
	public void onMessage(Message message) {
		// 取消息中的内容
		if (message instanceof TextMessage) {
			TextMessage textMessage = (TextMessage) message;
			// 取内容
			String text;
			try {
				text = textMessage.getText();
				System.out.println(text);
				// 其他业务逻辑...
			} catch (JMSException e) {
				e.printStackTrace();
			}
			
		}
	}

}
