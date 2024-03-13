package com.asouwn.core.registry;

import com.asouwn.core.spi.SpiLoader;

public class RegistryFactory {
    static {
        SpiLoader.load(Registry.class);
    }
    public static Registry getInstance(String key){
        return SpiLoader.getInstance(Registry.class, key);
    }
}
