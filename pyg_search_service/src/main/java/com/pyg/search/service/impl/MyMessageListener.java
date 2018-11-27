package com.pyg.search.service.impl;


import com.alibaba.fastjson.JSON;
import com.pyg.pojo.TbItem;
import com.pyg.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.List;
import java.util.Map;

/**
 * created by 沈小云 on 2018/10/30  16:33
 */
public class MyMessageListener implements MessageListener {
    @Autowired
    private ItemSearchService itemSearchService;
    @Override
    public void onMessage(Message message) {
        System.out.println("开始监听。。。。。");
        try {
            TextMessage textMessage = (TextMessage) message;
            String text = textMessage.getText();
            List<TbItem> tbItems = JSON.parseArray(text, TbItem.class);
            for (TbItem tbItem : tbItems) {
                String spec = tbItem.getSpec();
                Map map = JSON.parseObject(spec);
                tbItem.setSpecMap(map);
            }
            itemSearchService.importItemList(tbItems);
            System.out.println("导入成功");
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
