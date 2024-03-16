package com.asouwn.core.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ServerMetaInfo {
    private String serviceName;
    private String serviceVision;
    private String serviceHost;
    private String servicePort;

    /**
     * 服务地址
     * @return host:port
     */
    public String getAddress(){
        if (!serviceHost.contains("http"))
            return String.format("http://%s:%s", serviceHost, servicePort);
        return String.format("%s:%s", serviceHost, servicePort);
    }

    /**
     * 前置搜索的key关键词
     * @return serviceName/serviceVision
     */
    public String getPreKey(){
        return String.format("%s/%s", serviceName, serviceVision);
    }

    /**
     * 注册服务中心的key
     * @return serviceName:serviceVision/serviceHost:servicePort
     */
    public String getKey(){
        return String.format("%s/%s:%s", getPreKey(), serviceHost, servicePort);
    }
}
