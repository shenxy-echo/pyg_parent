package com.pyg.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.pyg.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;
import java.io.Serializable;
import java.util.Arrays;

/**
 * created by 沈小云 on 2018/10/30  17:04
 */
@Component
public class ItemDeleteListener implements MessageListener {
    @Autowired
    private ItemSearchService itemSearchService;
    @Override
    public void onMessage(Message message) {
        System.out.println("开始监听。。。");
        try{
            ObjectMessage objectMessage = (ObjectMessage) message;
            Long[] ids = (Long[ ]) objectMessage.getObject();
            itemSearchService.deleteByGoodsIds(Arrays.asList(ids));
            System.out.println("监听结束。。。");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
