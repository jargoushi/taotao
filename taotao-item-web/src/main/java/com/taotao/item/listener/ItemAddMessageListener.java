package com.taotao.item.listener;

import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import com.taotao.item.pojo.Item;
import com.taotao.pojo.TbItem;
import com.taotao.pojo.TbItemDesc;
import com.taotao.service.ItemService;

import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * 
* @ClassName: ItemAddMessageListener
* @Description: 监听商品添加时事件,然后生成商品详情页面
* @author ruwenbo
* @date 2017年10月1日 下午2:51:50
*
 */
public class ItemAddMessageListener implements MessageListener{

	@Autowired
	private ItemService itemServiceImpl;
	
	@Autowired
	private FreeMarkerConfigurer freeMarkerConfig;
	
	 @Value("${HTML_OUT_PATH}")
	 private String HTML_OUT_PATH;
	
	@Override
	public void onMessage(Message message) {
		
		try {
			// 从信息中取出商品id
			if (message instanceof TextMessage) {
				TextMessage textMessage = (TextMessage) message;
				String strItemId = textMessage.getText();
				Long itemId = null;
				if (StringUtils.isNotBlank(strItemId)) {
					itemId = new Long(strItemId);
				}
				// 根据商品id查询商品信息和商品描述
				 /*
	             * 等待事务的提交，采用三次尝试的机会
	             * 
	             * 根据商品id查询商品基本信息，这里需要注意的是消息发送方法
	             * 有可能还没有提交事务，因此这里是有可能取不到商品信息的，
	             * 为了避免这种情况出现，我们最好等待事务提交，这里我采用3次
	             * 尝试的方法，每尝试一次休眠一秒   
	             */
				
				TbItem tbItem = null;
				for (int i = 0; i < 3; i++) {
					Thread.sleep(3000);
					tbItem = itemServiceImpl.getItemByItemId(itemId);
					// 如果取到了商品基本信息，那么就退出循环
					if (tbItem != null) {
						break;
					}
				}
				Item item = new Item(tbItem);
				TbItemDesc itemDesc = itemServiceImpl.getItemDesc(itemId);
				// 创建数据集
				Map data = new HashMap();
				data.put("item", item);
				data.put("itemDesc", itemDesc);
				// 创建商品详情页面的模板
				// 指定静态文件输出目录
				Configuration configuration = freeMarkerConfig.createConfiguration();
				Template template = configuration.getTemplate("item.ftl");
				FileWriter writer = new FileWriter(new File(HTML_OUT_PATH + itemId + ".html"));
				// 生成静态文件
				template.process(data, writer);
				// 关闭流
				writer.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

}
