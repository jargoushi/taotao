package com.taotao.search.service.impl;

import java.util.List;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.taotao.common.pojo.SearchItem;
import com.taotao.common.utils.TaotaoResult;
import com.taotao.search.mapper.SearchItemMapper;
import com.taotao.search.service.SearchItemService;

/**
 * 
* @ClassName: SearchItemServiceImpl
* @Description: 商品数据导入到索引库
* @author ruwenbo
* @date 2017年9月22日 下午9:01:55
*
 */
@Service("searchItemService")
public class SearchItemServiceImpl implements SearchItemService{
	
	@Autowired
	private SearchItemMapper searchItemMapper;
	
	@Autowired
	private SolrServer solrServer;

	@Override
	public TaotaoResult importItemsToIndex() {
		try {
			// 1.先查询所有数据商品
			List<SearchItem> itemList = searchItemMapper.getItemList();
			// 2.遍历商品数据到索引库
			for (SearchItem searchItem : itemList) {
				// 3.创建文档对象
				SolrInputDocument document = new SolrInputDocument();
				// 4.把文档写入到索引库
				document.addField("id", searchItem.getId());
				document.addField("item_title", searchItem.getTitle());
				document.addField("item_sell_point", searchItem.getSell_point());
				document.addField("item_price", searchItem.getPrice());
				document.addField("item_image", searchItem.getImage());
				document.addField("item_category_name", searchItem.getCategory_name());
				document.addField("item_desc", searchItem.getItem_desc());
				solrServer.add(document);
			}
			// 5.提交
			solrServer.commit();
			// 6.返回成功
			return TaotaoResult.ok();
		} catch (Exception e) {
			e.printStackTrace();
			return TaotaoResult.build(500, "数据导入索引库失败");
		}
	}

	
}
