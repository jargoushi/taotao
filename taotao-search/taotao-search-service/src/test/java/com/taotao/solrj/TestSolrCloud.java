package com.taotao.solrj;

import org.apache.solr.client.solrj.impl.CloudSolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Test;

public class TestSolrCloud {

	@Test
	public void testSolrCloud() throws Exception {
		// 1.创建一个SolrServer对象，需要使用CloudSolrServer子类，它有一个构造方法
		// 构造方法有一个参数，叫做zkHost，是一个字符串类型，是zookeeper的地址列表
		CloudSolrServer solrServer = new CloudSolrServer("192.168.25.132:2181,192.168.25.132:2182,192.168.25.132:2183");
		// 2.需要指定默认的collection
		solrServer.setDefaultCollection("collection2");
		// 3.向索引库中添加文档，与单机版一样
		SolrInputDocument document = new SolrInputDocument();
		document.addField("id", "1");
		document.addField("item_title", "测试商品");
		document.addField("item_price", 199);
		// 4.提交
		solrServer.commit();
	}
	
	@Test
	public void testDeleteDocument() throws Exception {
		// 1.创建一个SolrServer对象，需要使用CloudSolrServer子类，它有一个构造方法
		// 构造方法有一个参数，叫做zkHost，是一个字符串类型，是zookeeper的地址列表
		CloudSolrServer solrServer = new CloudSolrServer("192.168.25.132:2181,192.168.25.132:2182,192.168.25.132:2183");
		// 2.需要指定默认的collection
		solrServer.setDefaultCollection("collection2");
		solrServer.deleteByQuery("*:*");
		// 3.提交
	    solrServer.commit();
	}
}
