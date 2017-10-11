package com.taotao.freemarker;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class FreemarkerTest {

	@Test
    public void testFreemarkerFirst() throws Exception {
        // 创建一个Configuration对象
        Configuration configuration = new Configuration(Configuration.getVersion());
        // 设置模板所在的目录
        configuration.setDirectoryForTemplateLoading(new File("F:/JavaWorkSpace/taotao-item-web/src/main/resources/ftl"));
        // 设置模板字符集
        configuration.setDefaultEncoding("UTF-8");
        // 加载模板文件
        // Template template = configuration.getTemplate("hello.ftl");
        Template template = configuration.getTemplate("first.htm");
        // 创建一个数据集
        Map data = new HashMap();
        data.put("hello", "Hello Freemarker!!!");
        data.put("title", "Hello Freemarker!!!");
        data.put("stu", new Student(1, "茹文博", 24, "河南三门峡市"));
        
        List<Student> stuList = new ArrayList<>();
        stuList.add(new Student(1,"茹文博1", 24, "河南省三门峡市"));
        stuList.add(new Student(2,"茹文博2", 24, "河南省三门峡市"));
        stuList.add(new Student(3,"茹文博3", 24, "河南省三门峡市"));
        stuList.add(new Student(4,"茹文博4", 24, "河南省三门峡市"));
        stuList.add(new Student(5,"茹文博5", 24, "河南省三门峡市"));
        data.put("stuList", stuList);
        data.put("date", new Date());
        data.put("myTest", "abc");

        // 设置模板输出的目录及输出的文件名
        // FileWriter writer = new FileWriter(new File("F:/temp/freemarker/hello.html"));
        FileWriter writer = new FileWriter(new File("F:/temp/freemarker/first.html"));
        // 生成文件
        template.process(data, writer);
        // 关闭流
        writer.close();
    }
}
