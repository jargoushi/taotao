package com.taotao.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.taotao.common.pojo.EasyUITreeNode;
import com.taotao.common.utils.TaotaoResult;
import com.taotao.content.service.ContentCategoryService;

/**
 * 
* @ClassName: ContentCategoryController
* @Description: 内容分类管理Controller
* @author ruwenbo
* @date 2017年8月31日 下午9:33:37
*
 */
@Controller
public class ContentCategoryController {

	@Autowired
	private ContentCategoryService contentCategoryService;
	
	@RequestMapping("/content/category/list")
	@ResponseBody
	public List<EasyUITreeNode> getContentCategoryList(
			@RequestParam(name="id", defaultValue = "0") long parentId) {
		List<EasyUITreeNode> categoryList = contentCategoryService.getContentCategoryList(parentId);
		return categoryList;
	}
	
	@RequestMapping("/content/category/create")
	@ResponseBody
	public TaotaoResult addContentCategory(Long parentId, String name) {
		TaotaoResult taotaoResult = contentCategoryService.addContentCategory(parentId, name);
		return taotaoResult;
	}
	
	@RequestMapping("/content/category/delete")
	public void deleteContentCategory(long id) {
		contentCategoryService.deleteContentCategory(id);
	}
}
