package com.pyg.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pyg.cart.service.CartService;
import com.pyg.entity.Result;
import com.pyg.pojogroup.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import util.CookieUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.cert.X509Certificate;
import java.util.List;

/**
 * created by 沈小云 on 2018/11/3  15:53
 */
@RestController
@RequestMapping("/cart")
public class CartController {
    @Reference(timeout=6000)
    private CartService cartService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;

    @RequestMapping("/findCartList")
    public  List<Cart> findCartList(){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        String cartListString = CookieUtil.getCookieValue(request, "cartList", "utf-8");
        if(cartListString==null||cartListString.equals("")){
            cartListString = "[]";
        }
        List<Cart> cartList_cookie = JSON.parseArray(cartListString, Cart.class);
        if("anonymousUser".equals(name)){
            return cartList_cookie;
        }else {
            List<Cart> redisCartList = cartService.findCartListFromRedis(name);
            if(cartList_cookie.size()==0){
                redisCartList = cartService.mergeCartList(cartList_cookie,redisCartList);
                CookieUtil.deleteCookie(request,response,"cartList");
                cartService.saveCartListToRedis(name,redisCartList);
            }
            return redisCartList;
        }
    }

    @CrossOrigin(origins="http://localhost:9105",allowCredentials="true")
    @RequestMapping("/addGoodsToCart")
    public Result addGoodsToCart(Long itemId, Integer num){
      /*  response.setHeader("Access-Control-Allow-Origin", "http://localhost:9105");
        response.setHeader("Access-Control-Allow-Credentials", "true");*/
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        try{
            List<Cart> cartList = findCartList();
            cartList = cartService.addGoodsToCartList(cartList, itemId, num);
            if("anonymousUser".equals(name)){
                String jsonString = JSON.toJSONString(cartList);
                CookieUtil.setCookie(request,response,"cartList",jsonString,3600*24,"utf-8");
            }else{
                cartService.saveCartListToRedis(name,cartList);
            }
            return new Result(true,"添加成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"添加失败");
        }

    }

}
