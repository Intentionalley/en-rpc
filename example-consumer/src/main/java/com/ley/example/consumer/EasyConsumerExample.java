package com.ley.example.consumer;

import com.ley.enrpc.proxy.ServiceProxyFactory;
import com.ley.example.common.model.User;
import com.ley.example.common.service.UserService;

/**
 * 简易服务消费者示例
 */
public class EasyConsumerExample {
    public static void main(String[] args) {
        UserService userService = ServiceProxyFactory.getProxy(UserService.class);
        User user = new User();
        user.setName("ley");
        User newUser = userService.getUser(user);
        if (newUser != null){
            System.out.println(newUser.getName());
        }else{
            System.out.println("newUser == null");
        }
    }
}