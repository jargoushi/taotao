package com.taotao.cart.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.taotao.common.pojo.CookieUtils;
import com.taotao.common.utils.TaotaoResult;
import com.taotao.pojo.TbItem;
import com.taotao.service.ItemService;

/**
 * 
* @ClassName: CartController
* @Description: TODO(这里用一句话描述这个类的作用)
* @author ruwenbo
* @date 2017年10月5日 下午6:45:50
*
 */
@Controller
public class CartController {

	@Autowired
	private ItemService itemService;
	
	@Value("${COOKIE_TT_CART}")
	private String COOKIE_TT_CART;
	
	@Value("${COOKIE_CART_EXPIRE}")
	private Integer COOKIE_CART_EXPIRE;
	
	@RequestMapping("/cart/add/{itemId}")
	public String addCart(@PathVariable Long itemId, Integer num,
			HttpServletRequest request, HttpServletResponse response) {
		// 先从cookie中查询购物车列表
		List<TbItem> cartList = getCartList(request);
		// 判断购物车中是否有此商品
		Boolean flag = false;
		for (TbItem tbItem : cartList) {
			/*
             * 由于tbItem的ID与参数中的itemId都是包装类型的Long，要比较是否相等不要用==，
             * 因为那样比较的是对象的地址而不是值，为了让它们比较的是值，那么可以使用.longValue来获取值 
             */
			if (tbItem.getId() == itemId.longValue()) {
				tbItem.setNum(tbItem.getNum() + num);
				flag = true;
				break;
			}
		}
		if (!flag) {
			// 如果没有，则根据商品id查询商品信息，调用商品服务实现
			TbItem tbItem = itemService.getItemByItemId(itemId);
			// 设置商品数量
			tbItem.setNum(num);
			// 取一张图片
			String image = tbItem.getImage();
			if (StringUtils.isNotBlank(image)) {
				tbItem.setImage(image.split(",")[0]);
			}
			// 添加到商品列表
			cartList.add(tbItem);
		}
		// 把购物车列表写入到cookie
		CookieUtils.setCookie(request, response, COOKIE_TT_CART, JSON.toJSONString(cartList), COOKIE_CART_EXPIRE, true);
		// 返回成功页面
		return "cartSuccess";
		
	}

	/**
	 * 
	* @Title: getCartList
	* @Description: 从cookie中取出购物车列表
	* @param @param request    设定文件
	* @return void    返回类型
	* @throws
	 */
	private List<TbItem> getCartList(HttpServletRequest request) {
		// 使用CookieUtils取出购物车列表
		String json = CookieUtils.getCookieValue(request, COOKIE_TT_CART, true);
		// 判断Cookie中是否有值
		if (StringUtils.isBlank(json)) {
			return new ArrayList<>();
		}
		List<TbItem> list = JSON.parseArray(json, TbItem.class);
		return list;
	}
	
	@RequestMapping("/cart/cart")
	public String showCartList(HttpServletRequest request) {
		// 从cookie中取出购物车列表
		List<TbItem> cartList = getCartList(request);
		// 把购物车列表传递给jsp页面
		request.setAttribute("cartList", cartList);
		// 返回逻辑视图
		return "cart";
	}
	
	/**
	 * 
	* @Title: uodateNum
	* @Description: 更新购物车商品数量
	* @param @return    设定文件
	* @return TaotaoResult    返回类型
	* @throws
	 */
	@RequestMapping("cart/update/num/{itemId}/{num}")
	@ResponseBody
	public TaotaoResult uodateNum(@PathVariable Long itemId,@PathVariable Integer num,
			HttpServletRequest request, HttpServletResponse response) {
		// 取购物车列表信息
		List<TbItem> cartList = getCartList(request);
		// 遍历商品列表找到商品信息
		for (TbItem tbItem : cartList) {
			if (tbItem.getId() == itemId.longValue()) {
				tbItem.setNum(num);
				break;
			}
		}
		// 写入到cookie
		CookieUtils.setCookie(request, response, COOKIE_TT_CART, JSON.toJSONString(cartList), COOKIE_CART_EXPIRE, true);
		//返回结果
		return TaotaoResult.ok();
	}
	
	/**
	 * 
	* @Title: deleteCartItem
	* @Description: 删除购物车商品
	* @param @return    设定文件
	* @return String    返回类型
	* @throws
	 */
	@RequestMapping("/cart/delete/{itemId}")
	public String deleteCartItem(@PathVariable Long itemId,
			HttpServletRequest request, HttpServletResponse response) {
		// 取购物车列表信息
		List<TbItem> cartList = getCartList(request);
		// 找到对应的商品
		for (TbItem tbItem : cartList) {
			if (tbItem.getId() == itemId.longValue()) {
				cartList.remove(tbItem);
				break;
			}
		}
		// 写入到cookie
		CookieUtils.setCookie(request, response, COOKIE_TT_CART,JSON.toJSONString(cartList), COOKIE_CART_EXPIRE, true);
		// 返回逻辑视图，需要做redirect重定向
		return "redirect:/cart/cart.html";
	}
}
