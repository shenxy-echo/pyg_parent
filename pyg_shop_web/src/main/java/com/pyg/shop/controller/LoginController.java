package com.pyg.shop.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * created by 沈小云 on 2018/10/13  8:52
 */
@RestController
@RequestMapping("/login")
public class LoginController {
    @RequestMapping("/loginName")
    public Map getLoginName(){
        String loginName = SecurityContextHolder.getContext().getAuthentication().getName();
        Map map = new HashMap();
        map.put("loginName",loginName);
        return map;
    }
}
