package com.asouwn.core.registry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 本地注册器
 */
public class LocalRegistry {
    private static final Map<String, Class<?>> m = new ConcurrentHashMap<>();

    /**
     * 注册服务
     * @param service 服务名
     * @param implClass 实现类
     */
    public static void registry(String service, Class<?> implClass){
        m.put(service, implClass);
    }

    /**
     * 删除服务
     * @param service 服务名
     */
    public static void disRegistry(String service){
        m.remove(service);
    }

    /**
     * 获取服务
     * @param service 服务名
     * @return 服务名指向的实现类
     */
    public static Class<?> getService(String service){
        return m.get(service);
    }
}
