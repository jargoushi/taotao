package com.taotao.content.service;

import java.util.List;

import com.taotao.common.pojo.EasyUIDataGraidResult;
import com.taotao.common.utils.TaotaoResult;
import com.taotao.pojo.TbContent;

public interface ContentService {

	EasyUIDataGraidResult findContentByCategoryId(long contentCategoryId, Integer page, Integer rows);
	
	TaotaoResult saveContent(TbContent content);
	
	TaotaoResult updateContent(TbContent content);
	
	TaotaoResult deleteContent(String ids);
	
	List<TbContent> getContentList(Long contentCategoryId);
}
