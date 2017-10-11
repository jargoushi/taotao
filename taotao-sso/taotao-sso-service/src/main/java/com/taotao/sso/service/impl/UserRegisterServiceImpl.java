package com.taotao.sso.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import com.taotao.common.utils.TaotaoResult;
import com.taotao.mapper.TbUserMapper;
import com.taotao.pojo.TbUser;
import com.taotao.pojo.TbUserExample;
import com.taotao.pojo.TbUserExample.Criteria;
import com.taotao.sso.service.UserRegisterService;

/**
 * 
* @ClassName: UserRegisterServiceImpl
* @Description: 用户注册处理Service
* @author ruwenbo
* @date 2017年10月3日 下午2:10:55
*
 */
@Service("userRegisterService")
public class UserRegisterServiceImpl implements UserRegisterService{

	@Autowired
	private TbUserMapper userMapper;

	@Override
	public TaotaoResult checkUserInfo(String param, int type) {
		TbUserExample example = new TbUserExample();
		Criteria criteria = example.createCriteria();
		// 判断要校验的数据类型，来设置不同的查询条件
		// 1.2.3分别代表userName,phone,email
		if (type == 1) {
			criteria.andUsernameEqualTo(param);
		} else if (type == 2) {
			criteria.andPhoneEqualTo(param);
		} else if (type == 3) {
			criteria.andEmailEqualTo(param);
		} else {
			return TaotaoResult.build(400, "参数中包含非法参数");
		}
		// 执行查询
		List<TbUser> list = userMapper.selectByExample(example);
		if (list == null || list.size() == 0) {
			// 查询到数据，返回false
			return TaotaoResult.ok(true);
		}
		// 数据可以使用
		return TaotaoResult.ok(false);
	}

	@Override
	public TaotaoResult createUser(TbUser user) {
		// 校验数据的合法性
		if (StringUtils.isBlank(user.getUsername()) || StringUtils.isBlank(user.getPassword())) {
			return TaotaoResult.build(400, "用户名或密码不能为空");
		}
		// 校验用户名是否重复
		TaotaoResult taotaoResult = checkUserInfo(user.getUsername(), 1);
		Boolean flag = (Boolean) taotaoResult.getData();
		if (!flag) {
			return TaotaoResult.build(400, "用户名重复");
		}
		// 校验手机号重复
		if (StringUtils.isNotBlank(user.getPhone())) {
			taotaoResult = checkUserInfo(user.getPhone(), 2);
			if (!(Boolean)taotaoResult.getData()) {
				return TaotaoResult.build(400, "手机号重复");
			}
		}
		// 校验邮箱是否重复
		if (StringUtils.isNotBlank(user.getEmail())) {
			taotaoResult = checkUserInfo(user.getEmail(), 3);
			if (!(Boolean)taotaoResult.getData()) {
				return TaotaoResult.build(400, "邮箱重复");
			}
		}
		// 补全TbUser对象的属性
		user.setCreated(new Date());
		user.setUpdated(new Date());
		// 把密码进行md5加密
		String password = DigestUtils.md5DigestAsHex(user.getPassword().getBytes());
		user.setPassword(password);
		// 插入到数据库
		userMapper.insert(user);
		return taotaoResult.ok();
	}
	
	
}
