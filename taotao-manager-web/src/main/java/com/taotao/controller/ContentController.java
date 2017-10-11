package com.taotao.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.taotao.common.pojo.EasyUIDataGraidResult;
import com.taotao.common.utils.TaotaoResult;
import com.taotao.content.service.ContentService;
import com.taotao.pojo.TbContent;

/**
 * 
* @ClassName: ContentController
* @Description: 内容管理Controller
* @author ruwenbo
* @date 2017年9月16日 下午9:46:38
*
 */
@Controller
public class ContentController {

	@Autowired
	private ContentService contentService;
	
	@RequestMapping("/content/query/list")
	@ResponseBody
	public EasyUIDataGraidResult findContentByCategoryId(Long categoryId, Integer page, Integer rows) {
		EasyUIDataGraidResult findContentByCategoryId = contentService.findContentByCategoryId(categoryId, page, rows);
		return findContentByCategoryId;
	}
	
	@RequestMapping("/content/save")
	@ResponseBody
	public TaotaoResult saveContent(TbContent content) {
		return contentService.saveContent(content);
	}
	
	@RequestMapping("/content/update")
	@ResponseBody
	public TaotaoResult updateContent(TbContent content) {
		return contentService.updateContent(content);
	}
	
	@RequestMapping("/content/delete")
	@ResponseBody
	public TaotaoResult deleteContent(String ids) {
		return contentService.deleteContent(ids);
	}
	
}
