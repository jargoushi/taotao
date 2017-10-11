package com.taotao.sso.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 
* @ClassName: PageController
* @Description: 登录注册页面展示Controller
* @author ruwenbo
* @date 2017年10月3日 下午11:06:52
*
 */
@Controller
public class PageController {
	
	@RequestMapping("/page/register")
	public String showRegister() {
		return "register";
	}
	
	@RequestMapping("/page/login")
	public String showLogin(String redirect, Model model) {
		model.addAttribute("redirect", redirect);
		return "login";
	}

}
