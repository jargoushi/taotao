package com.taotao.search.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.taotao.common.pojo.SearchResult;
import com.taotao.search.service.SearchService;

/**
 * 
* @ClassName: SearchController
* @Description: 商品搜索Controller
* @author ruwenbo
* @date 2017年9月24日 上午11:24:03
*
 */
@Controller
public class SearchController {

	@Autowired
	private SearchService searchService;
	
	@Value("${ITEM_ROWS}")
	private Integer ITEM_ROWS;
	
	@RequestMapping("/search")
	public String searchItem(@RequestParam("q") String queryString,
			@RequestParam(defaultValue = "1") Integer page, Model model) throws Exception{
		try {
			// 结果乱码问题
			queryString = new String(queryString.getBytes("ISO8859-1"),"UTF-8"); 
			// 调用服务搜索商品信息
			SearchResult result = searchService.search(queryString, page, ITEM_ROWS);
			// 使用Model向页面传递参数
			model.addAttribute("query", queryString);
			model.addAttribute("totalPages", result.getTotalPages());
			model.addAttribute("itemList", result.getItemList());
			model.addAttribute("page", page);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
		return "search";
	}
}
