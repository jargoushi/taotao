package com.taotao.controller;

import com.taotao.common.pojo.EasyUIDataGraidResult;
import com.taotao.common.utils.TaotaoResult;
import com.taotao.pojo.TbItem;
import com.taotao.service.ItemService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 商品管理Controller
 * Created by ruwenbo on 2017/8/21.
 */
@Controller
public class ItemController {

    @Autowired
    private ItemService itemService;

    @RequestMapping("/item/{itemId}")
    @ResponseBody
    public TbItem getItemById(@PathVariable Long itemId) {
        TbItem item = itemService.getItemByItemId(itemId);
        return  item;
    }
    
    @RequestMapping("/item/list")
    @ResponseBody
    public EasyUIDataGraidResult getItemList(Integer page, Integer rows){
    	return itemService.getItemList(page, rows);
    }
    
    @RequestMapping(value = "/item/save", method = RequestMethod.POST)
    @ResponseBody
    public TaotaoResult addItem(TbItem item, String desc) {
    	TaotaoResult taotaoResult = itemService.addItem(item, desc);
    	return taotaoResult;
    }
    
    @RequestMapping(value = "/rest/item/delete")
    @ResponseBody
    public TaotaoResult deleteItem(String ids) {
    	return itemService.deleteItem(ids);
    }
    
    @RequestMapping(value = "/rest/item/reshelf")
    @ResponseBody
    public TaotaoResult updateItemUp(String ids) {
    	return itemService.updateItemUp(ids);
    }
    
    @RequestMapping(value = "/rest/item/instock")
    @ResponseBody
    public TaotaoResult updateItemDown(String ids) {
    	return itemService.updateItemDown(ids);
    }
    
    @RequestMapping(value = "/rest/item/query/item/desc/{id}")
    @ResponseBody
    public TaotaoResult selectItemDesc(@PathVariable("id") long id) {
    	return itemService.selectItemById(id);
    }
}
