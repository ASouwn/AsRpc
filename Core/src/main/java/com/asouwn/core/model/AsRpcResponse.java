package com.asouwn.core.model;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class AsRpcResponse implements Serializable {
    /**
     * 响应的数据
     */
    private Object data;
    /**
     * 响应数据类型
     */
    private Class<?> dataType;
    /**
     * 响应的信息
     */
    private String msg;
    /**
     * 异常响应
     */
    private Exception exception;
}
