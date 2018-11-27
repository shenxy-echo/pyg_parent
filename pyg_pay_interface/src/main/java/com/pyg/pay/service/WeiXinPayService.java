package com.pyg.pay.service;

import java.util.Map;

/**
 * created by 沈小云 on 2018/11/6  15:35
 */
public interface WeiXinPayService {
    public Map createNative(String out_trade_no,String total_fee);

    public Map queryPayStatus(String out_trade_no);

    /**
     * 关闭支付
     * @param out_trade_no
     * @return
     */
    public Map closePay(String out_trade_no);
}
