package com.example.rpcapi.module;

import lombok.Data;
import lombok.ToString;

/**
 * @author yangheng <yangheng@kuaishou.com>
 * Created on 2023-07-12
 */
@Data
@ToString
public class RpcRequest {
    /**
     * ID of Request Object
     */
    private String requestId;
    /**
     * Class name
     */
    private String className;
    /**
     * Method name
     */
    private String methodName;
    /**
     * Parameter type
     */
    private Class<?>[] parameterTypes;
    /**
     * Participation
     */
    private Object[] parameters;
}
