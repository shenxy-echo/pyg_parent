package com.pyg.pay.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import com.pyg.pay.service.WeiXinPayService;
import org.springframework.beans.factory.annotation.Value;
import util.HttpClient;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * created by 沈小云 on 2018/11/6  15:36
 */
@Service
public class WeiXinPayServiceImpl implements WeiXinPayService {
    @Value("${appid}")
    private String appid;

    @Value("${partner}")
    private String partner;

    @Value("${partnerkey}")
    private String partnerkey;

    @Value("${notifyurl}")
    private String notifyurl;
    @Override
    /**
    * @author 沈小云
    * @description  todo:生成二维码目标url
    * @date 2018/11/6 15:52
    * @param
    *@return java.util.Map
    */
    public Map createNative(String out_trade_no, String total_fee) {
        try {
            //1、发送请求参数
            Map param = new HashMap();//请求参数
            param.put("appid",appid);//公众账号ID
            param.put("mch_id",partner);//商户号
            param.put("nonce_str",WXPayUtil.generateNonceStr());//随机字符串
            param.put("body","品优购商品订单");//商品描述
            param.put("out_trade_no",out_trade_no);//商户订单号
            param.put("total_fee",total_fee);//标价金额
            param.put("spbill_create_ip","192.168.75.180");//终端IP
            param.put("notify_url","https://www.jd.com");//通知地址
            param.put("trade_type","NATIVE");//交易类型

            //2、发送请求
            String url = "https://api.mch.weixin.qq.com/pay/unifiedorder";
            HttpClient httpClient =   new HttpClient(url);
            httpClient.setHttps(true);
            String xmlParam = WXPayUtil.generateSignedXml(param, partnerkey);
            httpClient.setXmlParam(xmlParam);
            httpClient.post();

            //3、获取请求结果
            String content = httpClient.getContent();
            Map<String, String> xmlToMap = WXPayUtil.xmlToMap(content);

            //4、封装返回结果
            Map resultMap = new HashMap();
            resultMap.put("out_trade_no",out_trade_no);
            resultMap.put("total_fee",total_fee);
            resultMap.put("code_url",xmlToMap.get("code_url"));
            return  resultMap;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public Map queryPayStatus(String out_trade_no) {
        try {
            //1、准备传递参数
            Map map = new HashMap();
            map.put("appid",appid);
            map.put("mch_id",partner);
            map.put("out_trade_no",out_trade_no);
            map.put("nonce_str",WXPayUtil.generateNonceStr());
            //2、发送请求
            String url = "https://api.mch.weixin.qq.com/pay/orderquery";
            HttpClient httpClient = new HttpClient(url);
            String signedXml = WXPayUtil.generateSignedXml(map, partnerkey);
            httpClient.setHttps(true);
            httpClient.setXmlParam(signedXml);
            httpClient.post();

            //3、获取返回结果
            String content = httpClient.getContent();
            Map<String, String> resultMap = WXPayUtil.xmlToMap(content);
            return resultMap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Map closePay(String out_trade_no) {
        Map param=new HashMap();
        param.put("appid", appid);//公众账号ID
        param.put("mch_id", partner);//商户号
        param.put("out_trade_no", out_trade_no);//订单号
        param.put("nonce_str", WXPayUtil.generateNonceStr());//随机字符串
        String url="https://api.mch.weixin.qq.com/pay/closeorder";
        try {
            String xmlParam = WXPayUtil.generateSignedXml(param, partnerkey);
            HttpClient client=new HttpClient(url);
            client.setHttps(true);
            client.setXmlParam(xmlParam);
            client.post();
            String result = client.getContent();
            Map<String, String> map = WXPayUtil.xmlToMap(result);
            System.out.println(map);
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
