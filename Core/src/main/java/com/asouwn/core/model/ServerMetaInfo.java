package com.asouwn.core.model;

public class ServerMetaInfo {
    private String serviceName;
    private String serviceHost;
    private String servicePort;

    public String getAddress(){
        if (!serviceHost.contains("http"))
            return String.format("http://%s:%s", serviceHost, servicePort);
        return String.format("%s:%s", serviceHost, servicePort);
    }
    public String getKey(){
        return String.format("%s/%s", serviceName, getAddress());
    }
}
