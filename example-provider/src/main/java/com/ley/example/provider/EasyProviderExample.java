package com.ley.example.provider;

import com.ley.enrpc.RpcApplication;
import com.ley.enrpc.registry.LocalRegistry;
import com.ley.enrpc.server.HttpServer;
import com.ley.enrpc.server.VertxHttpServer;
import com.ley.example.common.service.UserService;

/**
 * 简易服务提供者示例
 */
public class EasyProviderExample {
    public static void main(String[] args) {

        // RPC 框架初始化
        RpcApplication.init();
        // 注册服务
        LocalRegistry.register(UserService.class.getName(), UserServiceImpl.class);

        HttpServer httpServer = new VertxHttpServer();
        httpServer.doStart(RpcApplication.getRpcConfig().getServerPort());
    }
}
