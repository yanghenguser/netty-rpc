package com.example.rpcclient.proxy;

import java.lang.reflect.Method;
import java.util.UUID;

import org.springframework.cglib.proxy.InvocationHandler;

import com.example.rpcapi.module.RpcRequest;
import com.example.rpcapi.module.RpcResponse;
import com.example.rpcclient.client.NettyClient;

import lombok.extern.slf4j.Slf4j;

/**
 * @author yangheng <yangheng@kuaishou.com>
 * Created on 2023-07-12
 */
@Slf4j
public class RpcClientDynamicProxy<T> implements InvocationHandler {
    private Class<T> clazz;
    public RpcClientDynamicProxy(Class<T> clazz) throws Exception {
        this.clazz = clazz;
    }
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcRequest request = new RpcRequest();
        request.setRequestId(UUID.randomUUID().toString());
        String className = method.getDeclaringClass().getName();
        String methodName = method.getName();

        Class<?>[] parameterTypes = method.getParameterTypes();
        request.setClassName(className);
        request.setMethodName(methodName);
        request.setParameterTypes(parameterTypes);
        request.setParameters(args);
        log.info("request body : {}", request);

        // 连接
        NettyClient client = new NettyClient("127.0.0.1", 9999);
        log.info("start connect netty server");
        client.connect();
        RpcResponse response = client.send(request);
        log.info("recv response : {}", response);
        return response.getResult();
    }
}
