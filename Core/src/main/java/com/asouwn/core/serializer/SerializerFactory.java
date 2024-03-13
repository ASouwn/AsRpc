package com.asouwn.core.serializer;

import com.asouwn.core.spi.SpiLoader;

/**
 * 工厂模式
 */
public class SerializerFactory {
    static {
    SpiLoader.load(Serializer.class);
    }
    public static Serializer getInstance(String serializerKey){
        return SpiLoader.getInstance(Serializer.class, serializerKey);
    }

}
