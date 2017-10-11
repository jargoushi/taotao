package com.taotao.fastdfs;

import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.StorageServer;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.junit.Test;

import com.taotao.utils.FastDFSClient;

public class testFastDFS {

	@Test
	public void uploadFile() throws Exception { 
		// 1.向工程中添加jar包
		// 2.创建一个配置文件， 配置tracker服务器的地址
		// 3.加载配置文件
		ClientGlobal.init("F:/JavaWorkSpace/taotao-manager-web/src/main/resources/resource/client.conf");
		// 4.创建一个trackerclient对象
		TrackerClient trackerClient = new TrackerClient();
		// 5.使用trackerclient对象获得trackerserver对象
		TrackerServer trackerServer = trackerClient.getConnection();
		// 6.创建一个storageServer的引用
		StorageServer storageServer = null;
		// 7.创建一个storagerClient对象，trackerserver，storageServer两个参数
		StorageClient storageClient = new StorageClient(trackerServer, storageServer);
		// 8.使用storagerClient对象上传文件
		String[] strings = storageClient.upload_file("E:/picture/timg.jpg", "jpg", null);
		for (String string : strings) {
			System.out.println(string);
		}
	}
	 
	@Test
	public void testFastDFSClient() throws Exception {
		FastDFSClient fastDFSClient = new FastDFSClient("F:/JavaWorkSpace/taotao-manager-web/src/main/resources/resource/client.conf");
		String string = fastDFSClient.uploadFile("E:/picture/timg1.jpg");
		System.out.println(string);
	}
}
