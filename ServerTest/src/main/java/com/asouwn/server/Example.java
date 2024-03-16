package com.asouwn.server;

import com.asouwn.commonTest.UserServer;
import com.asouwn.core.config.RegistryConfig;
import com.asouwn.core.model.ServerMetaInfo;
import com.asouwn.core.registry.ETCDRegistry;
import com.asouwn.core.registry.LocalRegistry;
import com.asouwn.core.service.VertxHttpServer;

import java.util.concurrent.ExecutionException;

public class Example {
    public static void main(String[] args) {

//        todo 启动服务，注册服务，等待连接并响应
        ServerMetaInfo serverMetaInfo = ServerMetaInfo.builder()
                .serviceHost("localhost")
                .serviceName(UserServer.class.getName())
                .servicePort("8081")
                .serviceVision("1.0")
                .build();
        RegistryConfig registryConfig = RegistryConfig.builder()
                .registryAddr("http://localhost:2379")
                .registryName("etcd")
                .timeout(10000L)
                .build();
        ETCDRegistry etcdRegistry = new ETCDRegistry();
        etcdRegistry.init(registryConfig);
        try {
            etcdRegistry.register(serverMetaInfo);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        LocalRegistry.register(UserServer.class.getName(), Provider.class);
        new VertxHttpServer().serverStart(Integer.parseInt(serverMetaInfo.getServicePort()));
    }
}
