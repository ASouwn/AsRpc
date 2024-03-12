package com.asouwn.core.proxy;

import java.lang.reflect.Proxy;

public class JdkProxyFactory {
    public static <T> T getProxy(Class<T> serviceProxy){
        return (T) Proxy.newProxyInstance(
                serviceProxy.getClassLoader(),
                new Class[]{serviceProxy},
                new JdkProxy());
    }
}
