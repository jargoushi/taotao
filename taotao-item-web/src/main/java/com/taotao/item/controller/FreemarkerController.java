package com.taotao.item.controller;

import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import freemarker.template.Configuration;
import freemarker.template.Template;

@Controller
public class FreemarkerController {

	@Autowired
	private FreeMarkerConfigurer freeMarkerConfig;
	
	@RequestMapping("/genHtml")
	@ResponseBody
	public String genHtml() throws Exception {
		Configuration configuration = freeMarkerConfig.createConfiguration();
		Template template = configuration.getTemplate("hello.ftl");
		Map map = new HashMap();
		map.put("hello", "freemarker和spring整合测试");
		FileWriter writer = new FileWriter(new File("F:/temp/freemarker/hello.html"));
		template.process(map, writer);
		writer.close();
		return "ok";
	}
}
