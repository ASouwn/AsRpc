package com.asouwn.core.proxy;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.asouwn.core.config.RegistryConfig;
import com.asouwn.core.model.AsRpcRequest;
import com.asouwn.core.model.AsRpcResponse;
import com.asouwn.core.model.ServerMetaInfo;
import com.asouwn.core.registry.ETCDRegistry;
import com.asouwn.core.serializer.Serializer;
import com.asouwn.core.serializer.SerializerFactory;
import com.asouwn.core.serializer.SerializerKeys;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * 动态代理
 * 代替消费者构造申请包并获得响应
 */
public class JdkProxy implements InvocationHandler {
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
//        指定序列器
        final Serializer serializer = SerializerFactory.getInstance(SerializerKeys.JDK);

        ServerMetaInfo serverMetaInfo = ServerMetaInfo.builder()
                .serviceName(method.getDeclaringClass().getName())
                .build();
        RegistryConfig registryConfig = RegistryConfig.builder()
                .registryAddr("http://localhost:2379")
                .registryName("etcd").build();
        ETCDRegistry registry = new ETCDRegistry();
        registry.init(registryConfig);
//        构建申请包并序列化
        AsRpcRequest request = AsRpcRequest.builder()
                .serverName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .args(args)
                .parameterType(method.getParameterTypes())
                .build();
        try {
            List<ServerMetaInfo> value = registry.getValue(serverMetaInfo);

            byte[] serialized = serializer.serializer(request);
//        申请连接,这里没有设置负载均衡，默认为第一个服务的地址
            try(HttpResponse httpResponse = HttpRequest.post(value.get(0).getAddress())
                    .body(serialized).execute()) {
//            反序列化得到的响应包并得到包中的结果
                byte[] bytes = httpResponse.bodyBytes();

                AsRpcResponse deserializer = serializer.deserializer(bytes, AsRpcResponse.class);
                return deserializer.getData();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
