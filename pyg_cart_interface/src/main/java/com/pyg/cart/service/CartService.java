package com.pyg.cart.service;

import com.pyg.pojogroup.Cart;

import java.util.List;

/**
 * created by 沈小云 on 2018/11/3  15:11
 */
public interface CartService {
    public List<Cart>  addGoodsToCartList(List<Cart> cartList,Long itemId,Integer num);

    public List<Cart> findCartListFromRedis(String username);

    public void saveCartListToRedis(String username,List<Cart> cartList);

    public List<Cart> mergeCartList(List<Cart> cookieCartList,List<Cart> redisCartList);
}
