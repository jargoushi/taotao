package com.taotao.content.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.taotao.common.pojo.EasyUITreeNode;
import com.taotao.common.utils.TaotaoResult;
import com.taotao.content.service.ContentCategoryService;
import com.taotao.mapper.TbContentCategoryMapper;
import com.taotao.pojo.TbContentCategory;
import com.taotao.pojo.TbContentCategoryExample;
import com.taotao.pojo.TbContentCategoryExample.Criteria;

/**
 *
* @ClassName: ContentCategoryServiceImpl
* @Description: 内容分类管理Service
* @author ruwenbo
* @date 2017年8月31日 下午9:27:12
*
 */
@Service
public class ContentCategoryServiceImpl implements ContentCategoryService {

	@Autowired
	private TbContentCategoryMapper contentCategoryMapper;
	
	@Override
	public List<EasyUITreeNode> getContentCategoryList(long parentId) {
		// 设置查询条件
		TbContentCategoryExample example = new TbContentCategoryExample();
		Criteria criteria = example.createCriteria();
		criteria.andParentIdEqualTo(parentId);
		// 执行查询
		List<TbContentCategory> list = contentCategoryMapper.selectByExample(example);
		// 返回结果
		List<EasyUITreeNode> result = new ArrayList<>();
		for (TbContentCategory contentCategory : list) {
			EasyUITreeNode node = new EasyUITreeNode();
			node.setId(contentCategory.getId());
			node.setText(contentCategory.getName());
			node.setState(contentCategory.getIsParent() ? "closed" : "open");
			// 添加到返回结果列表
			result.add(node);
		}
		return result;
	}

	/*
	 * 新增内容分类
	* <p>Title: updateContentCategory</p>
	* <p>Description: </p>
	* @param id
	* @param name
	* @see com.taotao.content.service.ContentCategoryService#updateContentCategory(long, java.lang.String)
	 */
	@Override
	public TaotaoResult addContentCategory(long parentId, String name) {
		// 创建一个POJO对象
		TbContentCategory contentCategory = new TbContentCategory();
		// 补全对象的属性
		contentCategory.setParentId(parentId);
		contentCategory.setName(name);
		contentCategory.setCreated(new Date());
		contentCategory.setUpdated(new Date());
		// 状态 可选值:1.正常 2.删除
		contentCategory.setStatus(1);
		// 排序 默认为1
		contentCategory.setSortOrder(1);
		contentCategory.setIsParent(false);
		// 插入到数据库
		contentCategoryMapper.insert(contentCategory);
		// 判断父节点的状态
		TbContentCategory parent = contentCategoryMapper.selectByPrimaryKey(parentId);
		if(!parent.getIsParent()){
			// 如果父节点为叶子节点应改为父节点
			parent.setIsParent(true);
			// 更新父节点
			contentCategoryMapper.updateByPrimaryKey(parent);
		}
		// 返回结果
		return TaotaoResult.ok(contentCategory);
	}

	/*
	 * 重命名内容分类
	* <p>Title: updateContentCategory</p>
	* <p>Description: </p>
	* @param id
	* @param name
	* @see com.taotao.content.service.ContentCategoryService#updateContentCategory(long, java.lang.String)
	 */
	@Override
	public void updateContentCategory(long id, String name) {
		TbContentCategory contentCategory = contentCategoryMapper.selectByPrimaryKey(id);
		contentCategory.setName(name);
		contentCategory.setUpdated(new Date());
		contentCategoryMapper.updateByPrimaryKey(contentCategory);
	}

	/*
	 * 删除内容分类
	* <p>Title: deleteContentCategory</p>
	* <p>Description: </p>
	* @param id
	* @see com.taotao.content.service.ContentCategoryService#deleteContentCategory(long)
	 */
	@Override
	public void deleteContentCategory(long id) {
		try {
			TbContentCategory category = contentCategoryMapper.selectByPrimaryKey(id);
			// 首先判断要删除的contentCategory是否为父节点.如果是父节点，则删除下边所有的内容分类
			if (category.getIsParent()) {
				// 设置查询条件，查询出下边所有的内容分类
				TbContentCategoryExample example = new TbContentCategoryExample();
				Criteria criteria = example.createCriteria();
				criteria.andParentIdEqualTo(id);
				// 执行查询
				List<TbContentCategory> list = contentCategoryMapper.selectByExample(example);
				for (TbContentCategory tbContentCategory : list) {
					contentCategoryMapper.deleteByPrimaryKey(tbContentCategory.getId());
				}
				// 然后删除自身节点
				contentCategoryMapper.deleteByPrimaryKey(id);
			} else {
				// 只用删除自身节点即可
				contentCategoryMapper.deleteByPrimaryKey(id);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
