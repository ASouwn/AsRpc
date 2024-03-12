package com.asouwn.core.model;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Builder
@Data
public class AsRpcRequest implements Serializable {
    /**
     * 申请的服务名称
     */
    private String serverName;
    /**
     * 调用的方法名称
     */
    private String methodName;
    /**
     * 需要传送的参数
     */
    private Object[] args;
    /**
     * 传送参数的类型
     */
    private Class<?>[] parameterType;
}
