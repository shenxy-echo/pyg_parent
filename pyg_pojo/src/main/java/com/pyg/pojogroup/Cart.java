package com.pyg.pojogroup;

import com.pyg.pojo.TbOrderItem;

import java.io.Serializable;
import java.util.List;
/**
 * created by 沈小云 on 2018/11/3  15:55
 * TODO:创建一个购物车包装类
 */
public class Cart implements Serializable {
    private String sellerId;//商家id
    private String sellerName;//商家名称
    private List<TbOrderItem> orderItemList;

    public String getSellerId() {
        return sellerId;
    }

    /* * 
     * @param: sellerId
     * @return 
     * @description:
     * @date  
     */
    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public List<TbOrderItem> getOrderItemList() {
        return orderItemList;
    }

    public void setOrderItemList(List<TbOrderItem> orderItemList) {
        this.orderItemList = orderItemList;
    }
}
