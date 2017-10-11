package com.taotao.service;

import com.taotao.common.pojo.EasyUIDataGraidResult;
import com.taotao.common.utils.TaotaoResult;
import com.taotao.pojo.TbItem;
import com.taotao.pojo.TbItemDesc;

public interface ItemService {

	TbItem getItemByItemId(long itemId);
	
	EasyUIDataGraidResult getItemList(int page,int rows);
	
	TaotaoResult addItem(TbItem item, String desc);
	
	TaotaoResult deleteItem(String ids);
	
	TaotaoResult updateItemUp(String ids);
	
	TaotaoResult updateItemDown(String ids);
	
	TaotaoResult selectItemById(long id);
	
	TbItemDesc getItemDesc(long itemId);
}
