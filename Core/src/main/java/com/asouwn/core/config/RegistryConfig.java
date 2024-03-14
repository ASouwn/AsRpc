package com.asouwn.core.config;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegistryConfig {
    private String registryName;
    private String registryAddr;
    private long timeout;
}
