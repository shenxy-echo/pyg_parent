package com.pyg.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pyg.entity.Result;
import com.pyg.order.service.OrderService;
import com.pyg.pay.service.WeiXinPayService;
import com.pyg.pojo.TbPayLog;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import util.IdWorker;

import java.util.HashMap;
import java.util.Map;

/**
 * created by 沈小云 on 2018/11/6  16:42
 */
@RestController
@RequestMapping("/pay")
public class PayController {
    @Reference
    private WeiXinPayService weiXinPayService;

    @Reference
    private OrderService orderService;

    @RequestMapping("/createNative")
    public Map createNative(){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        TbPayLog tbPayLog = orderService.searchPayLogFromRedis(name);
        Map map = new HashMap();
        if(tbPayLog != null){
            map = weiXinPayService.createNative(tbPayLog.getOutTradeNo() + "", "1");
        }
        return map;
    }


    @RequestMapping("/queryPayStatus")
    public Result queryPayStatus(String out_trade_no){
        Result result = null;
        int x = 0;
        while (true){
            Map<String,String> map = weiXinPayService.queryPayStatus(out_trade_no);
            if(map==null){
                result = new Result(false,"支付失败");
                break;
            }
            if(("SUCCESS").equals(map.get("trade_state"))){
                result = new Result(true,"支付成功");
                orderService.updateOrderStatus(out_trade_no,map.get("transaction_id"));
                break;
            }
            //不频繁的访问，休息3秒后再次访问
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //为了不让循环无休止地运行，我们定义一个循环变量，如果这个变量超过了这个值则退出循环，设置时间为5分钟
            x++;
            if(x>=100){
                result=new  Result(false, "二维码超时");
                break;
            }
        }
        return result;
    }
}
