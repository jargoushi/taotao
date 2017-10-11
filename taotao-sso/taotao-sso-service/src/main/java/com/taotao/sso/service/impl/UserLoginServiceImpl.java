package com.taotao.sso.service.impl;

import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import com.alibaba.fastjson.JSON;
import com.taotao.common.utils.TaotaoResult;
import com.taotao.jedis.JedisClient;
import com.taotao.mapper.TbUserMapper;
import com.taotao.pojo.TbUser;
import com.taotao.pojo.TbUserExample;
import com.taotao.pojo.TbUserExample.Criteria;
import com.taotao.sso.service.UserLoginService;

/**
 * 
* @ClassName: UserLoginServiceImpl
* @Description: 用户登录处理Service
* @author ruwenbo
* @date 2017年10月3日 下午3:16:22
*
 */
@Service("userLoginService")
public class UserLoginServiceImpl implements UserLoginService{

	@Autowired
	private TbUserMapper userMapper;
	
	@Autowired
	private JedisClient jedisClient;
	
	 @Value("${SESSION_PRE}")
	 private String SESSION_PRE;

	 @Value("${SESSION_EXPIRE}")
	 private Integer SESSION_EXPIRE;
	
	@Override
	public TaotaoResult login(String userName, String password) {
		// 判断登录名及密码是否正确
		TbUserExample example = new TbUserExample();
		Criteria criteria = example.createCriteria();
		criteria.andUsernameEqualTo(userName);
		List<TbUser> list = userMapper.selectByExample(example);
		if (list == null || list.size() == 0) {
			return TaotaoResult.build(400, "用户名或密码错误");
		}
		// 校验密码，密码需要进行md5加密后再校验
		TbUser user = list.get(0);
		if (!DigestUtils.md5DigestAsHex(password.getBytes()).equals(user.getPassword())) {
			return TaotaoResult.build(400, "用户名或密码错误");
		}
		// 生成一个token
		String token = UUID.randomUUID().toString();
		// 把用户信息保存到redis数据库中
		// key就是token，value就是user对象转换成json
		user.setPassword(null);  	//为了安全就不把密码保存到redis数据库中了，因为这样太危险
		jedisClient.set(SESSION_PRE+":"+token, JSON.toJSONString(user));
		// 设置key的过期时间
		jedisClient.expire(SESSION_PRE+":"+token, SESSION_EXPIRE);
		return TaotaoResult.ok(token);
	}

	@Override
	public TaotaoResult getUserByToken(String token) {
		// 根据token到redis数据库中查询用户信息
		String json = jedisClient.get(SESSION_PRE+":"+token);
		if (StringUtils.isBlank(json)) {
			return TaotaoResult.build(400, "此用户登录已过期");
		}
		// 重置过期时间
		jedisClient.expire(SESSION_PRE+":"+token, SESSION_EXPIRE);
		// 转换成用户对象
		TbUser user = JSON.parseObject(json, TbUser.class);
		// 返回结果
		return TaotaoResult.ok(user);
	}

	@Override
	public TaotaoResult logout(String token) {
		jedisClient.expire(SESSION_PRE+":"+token, 0);
		return TaotaoResult.ok();
	}

}
