package com.ley.example.consumer;

import com.ley.enrpc.config.RpcConfig;
import com.ley.enrpc.proxy.ServiceProxyFactory;
import com.ley.enrpc.utils.ConfigUtil;
import com.ley.example.common.model.User;
import com.ley.example.common.service.UserService;

public class ConsumerExample {
    public static void main(String[] args) {
       RpcConfig rpc = ConfigUtil.loadConfig(RpcConfig.class, "rpc");
        System.out.println(rpc);
        // 获取代理
        UserService userService = ServiceProxyFactory.getProxy(UserService.class);
        User user = new User();
        user.setName("yupi");
        // 调用
        User newUser = userService.getUser(user);
        if (newUser != null) {
            System.out.println(newUser.getName());
        } else {
            System.out.println("user == null");
        }
    }
}
