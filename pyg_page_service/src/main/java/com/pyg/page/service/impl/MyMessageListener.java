package com.pyg.page.service.impl;

import com.pyg.page.service.ItemPageService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

/**
 * created by 沈小云 on 2018/10/30  17:17
 */
public class MyMessageListener implements MessageListener {
    @Autowired
    private ItemPageService itemPageService;
    @Override
    public void onMessage(Message message) {
        try{
            System.out.println("生成页面开始。。。");
            TextMessage textMessage = (TextMessage) message;
            String text = textMessage.getText();
            itemPageService.genItemHtml(Long.parseLong(text));
            System.out.println("text:"+text);
            System.out.println("生成页面结束。。。");
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
