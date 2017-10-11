package com.taotao.sso.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.taotao.common.pojo.CookieUtils;
import com.taotao.common.utils.TaotaoResult;
import com.taotao.pojo.TbUser;
import com.taotao.sso.service.UserLoginService;
import com.taotao.sso.service.UserRegisterService;

/**
 * 
* @ClassName: UserController
* @Description: 用戶管理Controller
* @author ruwenbo
* @date 2017年10月3日 下午2:38:53
*
 */
@Controller
public class UserController {
	
	@Value("${COOKIE_TOKEN_KEY}")
    private String COOKIE_TOKEN_KEY;

	@Autowired
	private UserRegisterService userRegisterService;
	
	@Autowired
	private UserLoginService userLoginService;
	
	@RequestMapping(value="/user/check/{param}/{type}", method=RequestMethod.GET)
	@ResponseBody
	public TaotaoResult checkUserInfo(@PathVariable String param, @PathVariable Integer type) {
		TaotaoResult taotaoResult = userRegisterService.checkUserInfo(param, type);
		return taotaoResult;
	}
	
	@RequestMapping(value="/user/register", method=RequestMethod.POST)
	@ResponseBody
	public TaotaoResult registerUser(TbUser user) {
		TaotaoResult taotaoResult = userRegisterService.createUser(user);
		return taotaoResult;
	}
	
	@RequestMapping(value="/user/login", method=RequestMethod.POST)
	@ResponseBody
	public TaotaoResult userLogin(String username, String password,
									HttpServletRequest request, HttpServletResponse response) {
		TaotaoResult taotaoResult = userLoginService.login(username, password);
		// 在返回结果前设置cookie
		// 1.cookie如何跨域
		// 2.如何设置cookie的有效期
		// 登陆成功后写cookie
		if (taotaoResult.getStatus() == 200) {
			// 取出token
			String token = taotaoResult.getData().toString();
			CookieUtils.setCookie(request, response, COOKIE_TOKEN_KEY, token);
		}
		// 返回结果
		return taotaoResult;
	}
	
	@RequestMapping(value="/user/token/{token}", produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ResponseBody
	public String getUserByToken(@PathVariable String token, String callback) {
		TaotaoResult result = userLoginService.getUserByToken(token);
		if (StringUtils.isNotBlank(callback)) {
			// 客户端为jsonp请求，需要返回js代码
			String jsonResult = callback + "(" + JSON.toJSONString(result) + ");";
			// 统一返回字符串
			return jsonResult;
		}
		return JSON.toJSONString(result).toString();
	}
	
	@RequestMapping("/user/logout/{token}")
	@ResponseBody
	public TaotaoResult logout(@PathVariable String token) {
		TaotaoResult taotaoResult = userLoginService.logout(token);
		return taotaoResult;
	}
}
