package com.taotao.content.service;

import java.util.List;

import com.taotao.common.pojo.EasyUITreeNode;
import com.taotao.common.utils.TaotaoResult;

public interface ContentCategoryService {

	List<EasyUITreeNode> getContentCategoryList(long parentId);
	
	TaotaoResult addContentCategory(long parentId, String name);
	
	void updateContentCategory(long id, String name);
	
	void deleteContentCategory(long id);
}
