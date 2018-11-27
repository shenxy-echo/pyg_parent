package com.pyg.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pyg.cart.service.CartService;
import com.pyg.mapper.TbItemMapper;
import com.pyg.pojo.TbItem;
import com.pyg.pojo.TbOrderItem;
import com.pyg.pojogroup.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * created by 沈小云 on 2018/11/3  15:08
 */
@Service
public class CartServiceImpl implements CartService {
    @Autowired TbItemMapper itemMapper;

    public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num) {
        //1、根据itemId获取tbItem,并根据tbItem获取sellerId和sellerName
        TbItem tbItem = itemMapper.selectByPrimaryKey(itemId);
        String sellerName = tbItem.getSeller();
        String sellerId = tbItem.getSellerId();

        //2、判断购物车中有没有该商家的商品
        Cart cart = searchCart(cartList,sellerId);
        //2.1、购物车没有该商家的商品
        if(cart == null){
            cart = new Cart();
            cart.setSellerId(sellerId);
            cart.setSellerName(sellerName);
            List<TbOrderItem> orderItemList = new ArrayList<>();
            TbOrderItem orderItem = createOrderItem(tbItem,num);
            orderItemList.add(orderItem);
            cart.setOrderItemList(orderItemList);
            cartList.add(cart);
        }else{
        //2.2、购物车有该商家的商品
            //2.2.1、获取该商家中该商品
            TbOrderItem orderItem = searchOrderItemFormCart(cart.getOrderItemList(),itemId);
                //2.2.1.1、该商家订单中没有该商品
            if(orderItem == null){
                orderItem = createOrderItem(tbItem, num);
                cart.getOrderItemList().add(orderItem);
            }else{
                //2.2.1.2、该商家订单中有该商品
                orderItem.setNum(orderItem.getNum()+num);//重置订单数
                orderItem.setTotalFee(new BigDecimal(orderItem.getPrice().doubleValue()*orderItem.getNum()));//重新计算小计
                //如果该订单数量数小于等于0，从该商家订单中，删除该订单
                if(orderItem.getNum()<=0){
                    cart.getOrderItemList().remove(orderItem);
                }
                //如果该商家订单数为0，从集合中删掉该商家
                if(cart.getOrderItemList().size() ==0){
                    cartList.remove(cart);
                }
            }
        }

        return cartList;
    }

    @Autowired
    private RedisTemplate redisTemplate;
    @Override
    public List<Cart> findCartListFromRedis(String username) {
        System.out.println("从redis中提取购物车数据....."+username);
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(username);
        if(cartList == null){
            cartList = new ArrayList<>();
        }
        return cartList;
    }

    @Override
    public void saveCartListToRedis(String username, List<Cart> cartList) {
        System.out.println("向redis中存储购物车数据....."+username);
        redisTemplate.boundHashOps("cartList").put(username,cartList);
    }

    private TbOrderItem searchOrderItemFormCart(List<TbOrderItem> orderItemList, Long itemId) {
        for (TbOrderItem orderItem : orderItemList) {
            if(orderItem.getItemId().equals(itemId)){
                return orderItem;
            }
        }
        return null;
    }

    private TbOrderItem createOrderItem(TbItem tbItem, Integer num) {
        TbOrderItem orderItem = new TbOrderItem();
        orderItem.setGoodsId(tbItem.getGoodsId());
        orderItem.setItemId(tbItem.getId());
        orderItem.setNum(num);
        orderItem.setPrice(tbItem.getPrice());
        orderItem.setPicPath(tbItem.getImage());
        orderItem.setTitle(tbItem.getTitle());
        orderItem.setSellerId(tbItem.getSellerId());
        orderItem.setTotalFee(new BigDecimal(tbItem.getPrice().doubleValue()*num));
        return orderItem;
    }

    private Cart searchCart(List<Cart> cartList, String sellerId) {
        for (Cart cart : cartList) {
            if(cart.getSellerId().equals(sellerId)){
                return cart;
            }
        }
        return null;
    }

    @Override
    public List<Cart> mergeCartList(List<Cart> cookieCartList, List<Cart> redisCartList) {
        for (Cart cart : cookieCartList) {
            for (TbOrderItem orderItem : cart.getOrderItemList()) {
                redisCartList = addGoodsToCartList(redisCartList, orderItem.getItemId(), orderItem.getNum());
            }
        }
        return redisCartList;
    }
}
