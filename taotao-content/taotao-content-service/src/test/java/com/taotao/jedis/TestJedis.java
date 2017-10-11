package com.taotao.jedis;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;

public class TestJedis {

	@Test
	public void testJedisSingle() {
		
		// 第一步:创建jedis对象，需要指定服务端的ip和端口号
		Jedis jedis = new Jedis("192.168.25.131", 6379);
		// 第二部:使用jedis对象操作数据库，每个redis命令对应一个方法
		jedis.set("mytest", "1000");
		// 第三步:打印结果
		String result = jedis.get("mytest");
		System.out.println(result);
		// 第三部:关闭jedis
		jedis.close();
	}
	
	@Test
	public void testJedisPool() {
		
		// 1.创建一个连接池对象
		JedisPool jedisPool = new JedisPool("192.168.25.131", 6379);
		// 2.从连接池中获取连接
		Jedis jedis = jedisPool.getResource();
		String result = jedis.get("mytest");
		System.out.println(result);
		// 3.每次jedis使用完毕后需要关闭连接。连接池回收资源
		jedis.close();
		// 4.系统结束前关闭连接池
		jedisPool.close();
	}
	
	@Test
	public void testJedisCluster() throws Exception {
		
		// 创建一个JedisCluster对象，构造参数Set类型，集合中每个参数是HostAndPort类型
		Set<HostAndPort> nodes = new HashSet<>();
		// 向集合中添加元素
		nodes.add(new HostAndPort("192.168.25.131", 7001));
		nodes.add(new HostAndPort("192.168.25.131", 7002));
		nodes.add(new HostAndPort("192.168.25.131", 7003));
		nodes.add(new HostAndPort("192.168.25.131", 7004));
		nodes.add(new HostAndPort("192.168.25.131", 7005));
		nodes.add(new HostAndPort("192.168.25.131", 7006));
		JedisCluster jedisCluster = new JedisCluster(nodes);
		// 直接使用jedisCluster对象操作redis，自带连接池，jedisCluster可以是单例的
		jedisCluster.set("cluster-test", "hello jedis cluster");
		String string = jedisCluster.get("cluster-test");
		System.out.println(string);
		// 系统关闭前关闭jedisCluster
		jedisCluster.close();
	}
}
