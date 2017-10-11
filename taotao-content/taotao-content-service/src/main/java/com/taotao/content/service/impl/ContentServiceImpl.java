package com.taotao.content.service.impl;

import java.util.Date;
import java.util.List;

import net.sf.jsqlparser.expression.LongValue;

import org.apache.commons.lang3.StringUtils;
import org.jboss.netty.handler.ipfilter.CIDR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.taotao.common.pojo.EasyUIDataGraidResult;
import com.taotao.common.utils.TaotaoResult;
import com.taotao.content.service.ContentService;
import com.taotao.jedis.JedisClient;
import com.taotao.mapper.TbContentMapper;
import com.taotao.pojo.TbContent;
import com.taotao.pojo.TbContentExample;
import com.taotao.pojo.TbContentExample.Criteria;

/**
 * 
* @ClassName: ContentServiceImpl
* @Description: 内容管理Service
* @author ruwenbo
* @date 2017年9月16日 下午9:38:52
*
 */
@Service
public class ContentServiceImpl implements ContentService{

	@Autowired
	private TbContentMapper contentMapper;
	
	@Autowired
	private JedisClient jedisClient;

	@Value("${INDEX_CONTENT}")
	private String iNDEX_CONTENT;

	@Override
	public EasyUIDataGraidResult findContentByCategoryId(
			long contentCategoryId, Integer page, Integer rows) {
		// 设置分页
		PageHelper.startPage(page, rows);
		// 设置查询条件
		TbContentExample example = new TbContentExample();
		Criteria criteria = example.createCriteria();
		criteria.andCategoryIdEqualTo(contentCategoryId);
		List<TbContent> list = contentMapper.selectByExampleWithBLOBs(example);
		// 设置返回结果
		EasyUIDataGraidResult easyUIDataGraidResult = new EasyUIDataGraidResult();
		easyUIDataGraidResult.setRows(list);
		// 取总记录数
		PageInfo<TbContent> pageInfo = new PageInfo<>(list);
		easyUIDataGraidResult.setTotal(pageInfo.getTotal());
		return easyUIDataGraidResult;
	}


	@Override
	public TaotaoResult saveContent(TbContent content) {
		// 补全POJO参数
		content.setCreated(new Date());
		content.setUpdated(new Date());
		try {
			contentMapper.insert(content);
			// 同步缓存
			// 删除对应的缓存信息
			jedisClient.hdel(iNDEX_CONTENT, String.valueOf(content.getCategoryId()));
			return TaotaoResult.ok();
		} catch (Exception e) {
			return TaotaoResult.build(500, "插入内容表失败");
		}
	}


	@Override
	public TaotaoResult updateContent(TbContent content) {
		// 补全POJO参数
		content.setCreated(new Date());
		content.setUpdated(new Date());
		// 更新content
		try {
			contentMapper.updateByPrimaryKey(content);
			// 同步缓存
			// 删除对应的缓存信息
			jedisClient.hdel(iNDEX_CONTENT, String.valueOf(content.getCategoryId()));
			return TaotaoResult.ok();
		} catch (Exception e) {
			return TaotaoResult.build(500, "更新内容失败");
		}
	}


	@Override
	public TaotaoResult deleteContent(String ids) {
		String[] id = ids.split(",");
		try {
			for (String string : id) {
				TbContent content = contentMapper.selectByPrimaryKey(Long.parseLong(string));
				// 同步缓存
				// 删除对应的缓存信息
				jedisClient.hdel(iNDEX_CONTENT, String.valueOf(content.getCategoryId()));
				contentMapper.deleteByPrimaryKey(Long.parseLong(string));
			}
			
			return TaotaoResult.ok();
		} catch (Exception e) {
			return TaotaoResult.build(500, "批量删除内容失败");
		}
	}


	@Override
	public List<TbContent> getContentList(Long contentCategoryId) {
		// 先查询缓存
		// 添加缓存不能影响业务逻辑
		try {
			// 查询缓存
			String json = jedisClient.hget(iNDEX_CONTENT, contentCategoryId + "");
			// 查询到结果，将json转换成list
			if(StringUtils.isNoneBlank(json)) {
				List<TbContent> list = JSON.parseArray(json, TbContent.class);
				return list;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 缓存没有命中，需要查询数据库
		// 设置查询条件
		TbContentExample example = new TbContentExample();
		Criteria criteria = example.createCriteria();
		criteria.andCategoryIdEqualTo(contentCategoryId);
		List<TbContent> list = contentMapper.selectByExample(example);
		
		// 将结果添加到缓存中
		try {
			jedisClient.hset(iNDEX_CONTENT, contentCategoryId + "", JSON.toJSONString(list));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

}
