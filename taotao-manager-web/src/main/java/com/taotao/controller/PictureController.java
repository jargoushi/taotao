package com.taotao.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.taotao.utils.FastDFSClient;

@Controller
public class PictureController {

	@Value("${IMAGE_SERVER_URL}")
	private String IMAGE_SERVER_URL;
	
	@RequestMapping("/pic/upload")
	@ResponseBody
	public String uploadPic(MultipartFile uploadFile) {
		try {
			// 首先接受页面上传递的文件
			byte[] content = uploadFile.getBytes();
			// 取出文件的扩展名
			String originalFilename = uploadFile.getOriginalFilename();
			String ext = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
			// 把这个内容上传到图片服务器
			FastDFSClient fastDFSClient = new FastDFSClient("classpath:resource/client.conf");
			String url = fastDFSClient.uploadFile(content,ext);
			// 从配置文件中取图片服务器的URl
			// 创建返回结果对象
			Map result = new HashMap();
			result.put("error", 0);
            result.put("url", IMAGE_SERVER_URL + url);
            // 返回结果
            return JSON.toJSONString(result);
		} catch (Exception e) {
			 e.printStackTrace();
	         Map result = new HashMap();
	         result.put("error", 1);
	         result.put("message", "图片上传失败");
	         return JSON.toJSONString(result);
		}
	} 
}
