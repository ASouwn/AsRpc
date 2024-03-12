package com.asouwn.server;

import com.asouwn.commonTest.UserServer;
import com.asouwn.core.registry.LocalRegistry;
import com.asouwn.core.service.VertxHttpServer;

public class Example {
    public static void main(String[] args) {

//        todo 启动服务，注册服务，等待连接并响应
        LocalRegistry.register(UserServer.class.getName(), Provider.class);
        new VertxHttpServer().serverStart(8080);
    }
}
