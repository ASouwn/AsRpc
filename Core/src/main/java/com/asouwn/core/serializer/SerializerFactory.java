package com.asouwn.core.serializer;

import java.util.HashMap;
import java.util.Map;

public class SerializerFactory {
    /**
     * 实现单例映射
     */
    private static final Map<String, Serializer> KEY_SERIALIZER_VALUE = new HashMap<String, Serializer>(){
        {
            put(SerializerKeys.JDK, new JdkSerializer());
        }
    };
    /**
     * 默认序列化器
     */
    private static final Serializer DEFAULT_SERIALIZER = KEY_SERIALIZER_VALUE.get(SerializerKeys.JDK);
    public static Serializer getInstance(String serializerKey){
        return KEY_SERIALIZER_VALUE.getOrDefault(serializerKey, DEFAULT_SERIALIZER);
    }

}
