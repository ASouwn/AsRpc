package com.asouwn.core.service;

import com.asouwn.core.model.AsRpcRequest;
import com.asouwn.core.model.AsRpcResponse;
import com.asouwn.core.registry.LocalRegistry;
import com.asouwn.core.serializer.Serializer;
import com.asouwn.core.serializer.SerializerFactory;
import com.asouwn.core.serializer.SerializerKeys;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class HttpServerHandler implements Handler<HttpServerRequest> {
    @Override
    public void handle(HttpServerRequest httpServerRequest) {
//        指定序列器并反序列化得到的申请
        final Serializer serializer = SerializerFactory.getInstance(SerializerKeys.JDK);

        httpServerRequest.bodyHandler(body->{

            AsRpcRequest asRpcRequest ;
            try {
                asRpcRequest = serializer.deserializer(body.getBytes(), AsRpcRequest.class);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
//        构造响应对象,并调用注册器，找到相应类
            AsRpcResponse asRpcResponse = AsRpcResponse.builder().build();

            try {
                Class<?> implClass = LocalRegistry.getService(asRpcRequest.getServerName());
                Method method =implClass.getMethod(asRpcRequest.getMethodName(), asRpcRequest.getParameterType());
                Object invoked = method.invoke(implClass.newInstance(), asRpcRequest.getArgs());

                asRpcResponse.setData(invoked);
                asRpcResponse.setMsg("return the result");
                asRpcResponse.setDataType(method.getReturnType());

                doResponse(httpServerRequest, asRpcResponse, serializer);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException |
                     InstantiationException | IOException e) {
                throw new RuntimeException(e);
            }
        });

    }

    /**
     * 响应行为
     * @param request
     * @param asRpcResponse
     * @param serializer
     * @throws IOException
     */
    public void doResponse(HttpServerRequest request, AsRpcResponse asRpcResponse, Serializer serializer) throws IOException {
        HttpServerResponse response = request.response();
        byte[] serialized = serializer.serializer(asRpcResponse);
        response.end(Buffer.buffer(serialized));
    }
}
