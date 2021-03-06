package com.taotao.item.pojo;

import org.apache.commons.lang3.StringUtils;

import com.taotao.pojo.TbItem;

public class Item extends TbItem {
	
	public Item(TbItem tbItem) {
		// 初始化属性
		 this.setBarcode(tbItem.getBarcode());
	     this.setCid(tbItem.getCid());
	     this.setCreated(tbItem.getCreated());
	     this.setId(tbItem.getId());
	     this.setImage(tbItem.getImage());
	     this.setNum(tbItem.getNum());
	     this.setPrice(tbItem.getPrice());
	     this.setSellPoint(tbItem.getSellPoint());
	     this.setStatus(tbItem.getStatus());
	     this.setTitle(tbItem.getTitle());
	     this.setUpdated(tbItem.getUpdated());
	}
	
	public String[] getImages() {
		String image = this.getImage();
		if (StringUtils.isNotBlank(image)) {
			return image.split(",");
		}
		return null;
	}

}
