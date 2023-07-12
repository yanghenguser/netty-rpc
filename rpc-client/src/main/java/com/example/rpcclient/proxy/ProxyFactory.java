package com.example.rpcclient.proxy;

import org.springframework.cglib.proxy.Proxy;

/**
 * @author yangheng <yangheng@kuaishou.com>
 * Created on 2023-07-12
 */
public class ProxyFactory {
    public static <T> T create(Class<T> interfaceClass) throws Exception {
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(),
                new Class<?>[] {interfaceClass},
                new RpcClientDynamicProxy<T>(interfaceClass));
    }
}
