package com.taotao.search.exception;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

public class ClobalExceptionResolver implements HandlerExceptionResolver{

	private static final Logger LOGGER = Logger.getLogger(ClobalExceptionResolver.class);
	
	@Override
	public ModelAndView resolveException(HttpServletRequest arg0,
			HttpServletResponse arg1, Object arg2, Exception e) {
		// 写日志文件
		LOGGER.error("运行时异常", e);
		// 发短信、发邮件
        // 发短信：调用第三方运营商服务；发邮件使用jmail包
        // 跳转到友好地错误页面
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("error/exception");
		modelAndView.addObject("message", "您的网络异常，请稍后重试么么哒");
		return modelAndView;
	}

}
