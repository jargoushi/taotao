package com.taotao.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.taotao.common.pojo.EasyUIDataGraidResult;
import com.taotao.common.utils.IDUtils;
import com.taotao.common.utils.TaotaoResult;
import com.taotao.jedis.JedisClient;
import com.taotao.mapper.TbItemDescMapper;
import com.taotao.mapper.TbItemMapper;
import com.taotao.pojo.TbItem;
import com.taotao.pojo.TbItemDesc;
import com.taotao.pojo.TbItemExample;
import com.taotao.service.ItemService;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

/**
 * Created by ruwenbo on 2017/8/21.
 */
@Service
public class ItemServiceImpl implements ItemService{

    @Autowired
    private TbItemMapper itemMapper;
    
    @Autowired
    private TbItemDescMapper itemDescMapper;
    
    @Autowired
    private JmsTemplate jmsTemplate;
    
    @Autowired
    private Topic topic;
    
    @Autowired
    private JedisClient jedisClient;
    
    @Value("${REDIS_ITEM_KEY}")
    private String REDIS_ITEM_KEY;
    
    @Value("${REDIS_ITEM_EXPIRE}")
    private Integer REDIS_ITEM_EXPIRE;

    @Override
    public TbItem getItemByItemId(long itemId) {
    	// 先查询缓存
    	try {
			String json = jedisClient.get(REDIS_ITEM_KEY + ":" + itemId + ":BASE");
			if (StringUtils.isNotBlank(json)) {
				TbItem tbItem = JSON.parseObject(json, TbItem.class);
				return tbItem;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    	// 如果缓存没有命中，则查询数据库
        TbItem item = itemMapper.selectByPrimaryKey(itemId);
        
        // 添加到缓存
        try {
			jedisClient.set(REDIS_ITEM_KEY + ":" + itemId + ":BASE", JSON.toJSONString(item));
			// 设置过期时间
			jedisClient.expire(REDIS_ITEM_KEY + ":" + itemId + ":BASE", REDIS_ITEM_EXPIRE);
		} catch (Exception e) {
			e.printStackTrace();
		}
        return item;
    }

    /*
     * 商品列表分页展示
    * <p>Title: getItemList</p>
    * <p>Description: </p>
    * @param page 起始页
    * @param rows 结束页
    * @return
    * @see com.taotao.service.ItemService#getItemList(int, int)
     */
	@Override
	public EasyUIDataGraidResult getItemList(int page, int rows) {
		// 设置分页信息
		PageHelper.startPage(page, rows);
		// 执行查询
		TbItemExample example = new TbItemExample();
		List<TbItem> list = itemMapper.selectByExample(example);
		EasyUIDataGraidResult result = new EasyUIDataGraidResult();
		// 取查询结果
		PageInfo<TbItem> pageInfo = new PageInfo<>(list);
		result.setTotal(pageInfo.getTotal());
		result.setRows(list);
		return result;
	}

	/*
	 * 新增商品
	* <p>Title: addItem</p>
	* <p>Description: </p>
	* @param item 商品POJO
	* @param desc 商品描述
	* @return
	* @see com.taotao.service.ItemService#addItem(com.taotao.pojo.TbItem, java.lang.String)
	 */
	@Override
	public TaotaoResult addItem(TbItem item, String desc) {
		try {
			// 生成商品id
			final long itemId = IDUtils.genItemId();
			// 补全item的属性
			item.setId(itemId);
			// 商品状态:1.正常 2.下架 3.删除
			item.setStatus((byte) 1);
			item.setCreated(new Date());
			item.setUpdated(new Date());
			// 向item表插入数据
			itemMapper.insert(item);
			// 补全item_desc的属性
			TbItemDesc itemDesc = new TbItemDesc();
			itemDesc.setItemId(itemId);
			itemDesc.setItemDesc(desc);
			itemDesc.setCreated(new Date());
			itemDesc.setUpdated(new Date());
			// 向item_desc表插入数据
			itemDescMapper.insert(itemDesc);
			
			// 商品添加完成之后发送一个MQ消息
			jmsTemplate.send(topic, new MessageCreator() {
				
				@Override
				public Message createMessage(Session session) throws JMSException {
					// 创建一个消息对象
					// 要在匿名内部类访问局部变量itemId, itemId需要用final修饰
					TextMessage message = session.createTextMessage(itemId + "");
					return message;
				}
			});
			// 返回结果
			return TaotaoResult.ok();
		} catch (Exception e) {
			e.printStackTrace();
			return TaotaoResult.build(500, "商品表或者商品描述表插入失败");
		}
	}

	/*
	 * 批量删除商品
	* <p>Title: deleteItem</p>
	* <p>Description: </p>
	* @param ids 商品的多个id字符串,以‘，’分割
	* @return
	* @see com.taotao.service.ItemService#deleteItem(java.lang.String)
	 */
	@Override
	public TaotaoResult deleteItem(String ids) {
		try {
			// 遍历要删除的商品主键id
			String[] itemIds = ids.split(",");
			for (int i = 0; i < itemIds.length; i++) {
				itemMapper.deleteByPrimaryKey(Long.parseLong(itemIds[i]));
			}
			return TaotaoResult.ok();
		} catch (Exception e) {
			e.printStackTrace();
			return TaotaoResult.build(500, "删除商品失败");
		}
	}

	/*
	 * 批量上架商品
	* <p>Title: deleteItem</p>
	* <p>Description: </p>
	* @param ids 商品的多个id字符串,以‘，’分割
	* @return
	* @see com.taotao.service.ItemService#deleteItem(java.lang.String)
	 */
	@Override
	public TaotaoResult updateItemUp(String ids) {
		try {
			String[] itemIds = ids.split(",");
			int i = 0;
			for (int j = 0; j < itemIds.length; j++) {
				TbItem item = itemMapper.selectByPrimaryKey(Long.parseLong(itemIds[j]));
				// 判断商品的状态是否为上架状态
				if (item != null && item.getStatus().equals((byte)1)) {
					continue;
				} else {
					item.setStatus((byte) 1);
					itemMapper.updateByPrimaryKey(item);
					i++;
				}
			}
			return TaotaoResult.ok("商品上架成功，共上架" + i + "个商品");
		} catch (Exception e) {
			e.printStackTrace();
			return TaotaoResult.build(500, "商品上架失败啦啦啦啦");
		}
		
	}

	/*
	 * 批量商品下架
	* <p>Title: updateItemDown</p>
	* <p>Description: </p>
	* @param ids
	* @return
	* @see com.taotao.service.ItemService#updateItemDown(java.lang.String)
	 */
	@Override
	public TaotaoResult updateItemDown(String ids) {
		try {
			String[] itemIds = ids.split(",");
			int i = 0;
			for (int j = 0; j < itemIds.length; j++) {
				TbItem item = itemMapper.selectByPrimaryKey(Long.parseLong(itemIds[j]));
				// 判断商品是否为下架状态
				if (item != null && item.getStatus().equals((byte)2)) {
					continue;
				} else {
					item.setStatus((byte)2);
					itemMapper.updateByPrimaryKey(item);
					i++;
				}
			}
			return TaotaoResult.ok("商品下架成功,共下架" + i + "个商品");
		} catch (Exception e) {
			e.printStackTrace();
			return TaotaoResult.build(500, "商品下架失败了猪头啦啦啦啦");
		}
	}

	/*
	 * 通过商品id查询商品信息
	* <p>Title: selectItemById</p>
	* <p>Description: </p>
	* @param id
	* @return
	* @see com.taotao.service.ItemService#selectItemById(long)
	 */
	@Override
	public TaotaoResult selectItemById(long id) {
		try {
			TbItemDesc itemDesc = itemDescMapper.selectByPrimaryKey(id);
			return TaotaoResult.ok(itemDesc);
		} catch (Exception e) {
			e.printStackTrace();
			return TaotaoResult.build(500, "商品描述查询失败啦啦啦啦啦");
		}
	}

	@Override
	public TbItemDesc getItemDesc(long itemId) {
		// 先查询缓存
		try {
			String string = jedisClient.get(REDIS_ITEM_KEY + ":" + itemId + ":DESC");
			if (StringUtils.isNotBlank(string)) {
				TbItemDesc itemDesc = JSON.parseObject(string, TbItemDesc.class);
				return itemDesc;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// 如果缓存没有命中，那么查询数据库
		TbItemDesc itemDesc = itemDescMapper.selectByPrimaryKey(itemId);
		
		// 添加到缓存
		try {
			jedisClient.set(REDIS_ITEM_KEY + ":" + itemId + ":DESC", JSON.toJSONString(itemDesc));
			// 设置过期时间
			jedisClient.expire(REDIS_ITEM_KEY + ":" + itemId + ":DESC", REDIS_ITEM_EXPIRE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return itemDesc;
	}
    
    
}
