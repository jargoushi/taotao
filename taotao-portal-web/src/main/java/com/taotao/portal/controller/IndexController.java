package com.taotao.portal.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSON;
import com.taotao.content.service.ContentService;
import com.taotao.pojo.TbContent;
import com.taotao.portal.pojo.Ad1Node;

/**
 * 
 * @ClassName: IndexController
 * @Description: 首页展示Controller
 * @author ruwenbo
 * @date 2017年8月31日 下午8:02:49
 * 
 */
@Controller
public class IndexController {

	@Value("${AD1_CONTENT_CID}")
	private Long AD1_CONTENT_CID;

	@Value("${AD1_WIDTH}")
	private Integer AD1_WIDTH;

	@Value("${AD1_WIDTH_B}")
	private Integer AD1_WIDTH_B;

	@Value("${AD1_HEIGHT}")
	private Integer AD1_HEIGHT;

	@Value("${AD1_HEIGHT_B}")
	private Integer AD1_HEIGHT_B;
	
	@Autowired
	private ContentService contentService;

	@RequestMapping("/index")
	public String showIndex(Model model) {
		// 取内容分类id，从配置文件中读取
		// 根据分类id查询内容列表
		List<TbContent> contentList = contentService.getContentList(AD1_CONTENT_CID);
		List<Ad1Node> resultList = new ArrayList<>();
		for (TbContent content : contentList) {
			 Ad1Node node = new Ad1Node();
	            node.setAlt(content.getSubTitle());
	            node.setHref(content.getUrl());
	            node.setSrc(content.getPic());
	            node.setSrcB(content.getPic2());
	            node.setHeight(AD1_HEIGHT);
	            node.setHeightB(AD1_HEIGHT_B);
	            node.setWidth(AD1_WIDTH);
	            node.setWidthB(AD1_WIDTH_B);

	            resultList.add(node);
		}
		// 将List集合转换为json字符串
		String jsonString = JSON.toJSONString(resultList);
		model.addAttribute("ad1", jsonString);
		return "index";
	}
}
