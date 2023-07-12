package com.example.rpcapi.module;

import lombok.Data;
import lombok.ToString;

/**
 * @author yangheng <yangheng@kuaishou.com>
 * Created on 2023-07-12
 */
@Data
@ToString
public class RpcResponse {
    /**
     * Response ID
     */
    private String requestId;
    /**
     * error message
     */
    private String error;
    /**
     * Return results
     */
    private Object result;
}
