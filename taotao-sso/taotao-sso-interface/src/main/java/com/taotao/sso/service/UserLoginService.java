package com.taotao.sso.service;

import com.taotao.common.utils.TaotaoResult;

public interface UserLoginService {

	TaotaoResult login(String userName, String password);
	
	TaotaoResult getUserByToken(String token);
	
	// 安全退出
	TaotaoResult logout(String token);
}
