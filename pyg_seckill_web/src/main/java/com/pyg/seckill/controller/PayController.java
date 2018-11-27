package com.pyg.seckill.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pyg.entity.Result;
import com.pyg.pay.service.WeiXinPayService;
import com.pyg.pojo.TbSeckillOrder;
import com.pyg.seckill.service.SeckillOrderService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * created by 沈小云 on 2018/11/9  0:08
 */
@RestController
@RequestMapping("/pay")
public class PayController {
    @Reference
    private WeiXinPayService weiXinPayService;

    @Reference
    private SeckillOrderService seckillOrderService;

    @RequestMapping("/createNative")
    public Map createNative(){
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        TbSeckillOrder tbSeckillOrder = seckillOrderService.searchOrderFromRedisByUserId(userId);
        if(tbSeckillOrder !=null){
            long fen = (long) (tbSeckillOrder.getMoney().doubleValue()*100);
            return weiXinPayService.createNative(tbSeckillOrder.getId()+"",fen+"");
        }else{
            return new HashMap();
        }
    }

    /**
     * 查询支付状态
     * @param out_trade_no
     * @return
     */
    @RequestMapping("/queryPayStatus")
    public Result queryPayStatus(String out_trade_no){
        //获取当前用户
        String userId=SecurityContextHolder.getContext().getAuthentication().getName();
        Result result=null;
        int x=0;
        while(true){
            //调用查询接口
            Map<String,String> map = weiXinPayService.queryPayStatus(out_trade_no);
            if(map==null){//出错
                result=new  Result(false, "支付出错");
                break;
            }
            if(map.get("trade_state").equals("SUCCESS")){//如果成功
                result=new  Result(true, "支付成功");
                seckillOrderService.saveOrderFromRedisToDb(userId, Long.valueOf(out_trade_no), map.get("transaction_id"));
                break;
            }
            try {
                Thread.sleep(3000);//间隔三秒
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //不让循环无休止地运行定义变量，如果超过了这个值则退出循环，设置时间为1分钟
            x++;
            if(x>20){
                result=new  Result(false, "二维码超时");
                //1.调用微信的关闭订单接口（学员实现）
                Map<String,String> payresult = weiXinPayService.closePay(out_trade_no);
                if( !"SUCCESS".equals(payresult.get("result_code")) ){//如果返回结果是正常关闭
                    if("ORDERPAID".equals(payresult.get("err_code"))){
                        result=new Result(true, "支付成功");
                        seckillOrderService.saveOrderFromRedisToDb(userId, Long.valueOf(out_trade_no), map.get("transaction_id"));
                    }
                }
                if(result.isSuccess()==false){
                    System.out.println("超时，取消订单");
                    //2.调用删除
                    seckillOrderService.deleteOrderFromRedis(userId, Long.valueOf(out_trade_no));
                }
                break;
            }
        }
        return result;
    }
}
