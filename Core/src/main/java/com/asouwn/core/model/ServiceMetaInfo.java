package com.asouwn.core.model;

public class ServiceMetaInfo {
    /**
     * 服务名称
     */
    private String serviceName;
    /**
     * 服务版本号
     */
    private String serviceVersion;
    /**
     * 服务地址
     */
    private String serviceAddr;
    /**
     * 服务分组（未实现）
     */
    private String serviceGroup;

    /**
     * 获取服务键名
     * @return
     */
    public String getServiceKey(){
        return String.format("%s:%s", serviceName, serviceVersion);
    }

    /**
     * 获取节点键名
     * @return
     */
    public String getServiceNodeKey(){
        return String.format("%s/%s", getServiceKey(), serviceAddr);
    }
}
