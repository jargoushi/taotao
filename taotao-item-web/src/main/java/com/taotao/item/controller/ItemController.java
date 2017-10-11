package com.taotao.item.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.taotao.item.pojo.Item;
import com.taotao.pojo.TbItem;
import com.taotao.pojo.TbItemDesc;
import com.taotao.service.ItemService;

/**
 * 
* @ClassName: ItemController
* @Description: 商品详情页面展示处理Controller
* @author ruwenbo
* @date 2017年9月30日 下午7:41:14
*
 */
@Controller
public class ItemController {

	@Autowired
	private ItemService itemServiceImpl;
	
	@RequestMapping("/item/{itemId}")
	public String showItemInfo(@PathVariable Long itemId, Model model) {
		// 根据商品id查询商品基本信息
		TbItem tbItem = itemServiceImpl.getItemByItemId(itemId);
		// 根据TbItem初始化Item
		Item item = new Item(tbItem);
		// 根据商品id查询商品描述
		TbItemDesc desc = itemServiceImpl.getItemDesc(itemId);
		// 传递给页面
		model.addAttribute("item", item);
		model.addAttribute("itemDesc", desc);
		// 返回逻辑视图
		return "item";
	}
}
