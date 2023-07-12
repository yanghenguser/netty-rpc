package com.example.rpcserver.server;

import java.lang.reflect.InvocationTargetException;

import org.springframework.beans.BeansException;
import org.springframework.cglib.reflect.FastClass;
import org.springframework.cglib.reflect.FastMethod;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;


import com.example.rpcapi.module.RpcRequest;
import com.example.rpcapi.module.RpcResponse;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yangheng <yangheng@kuaishou.com>
 * Created on 2023-07-12
 */
@Component
@Slf4j
@ChannelHandler.Sharable
public class ServerHandler extends SimpleChannelInboundHandler<RpcRequest> implements ApplicationContextAware {
    private ApplicationContext applicationContext;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest msg) throws Exception {
        RpcResponse response = new RpcResponse();
        response.setRequestId(msg.getRequestId());
        try {
            Object handle = handler(msg);
            log.info("Get response : {} ", handle);
            response.setResult(handle);
        } catch (Throwable throwable) {
            response.setError(throwable.toString());
            throwable.printStackTrace();
        }
        ctx.writeAndFlush(response);
    }

    private Object handler(RpcRequest request) throws ClassNotFoundException, InvocationTargetException {
        //Use Class.forName to load Class files
        Class<?> clazz = Class.forName(request.getClassName());
        Object serviceBean = applicationContext.getBean(clazz);
        log.info("serviceBean: {}",serviceBean);
        Class<?> serviceClass = serviceBean.getClass();
        log.info("serverClass:{}",serviceClass);
        String methodName = request.getMethodName();

        Class<?>[] parameterTypes = request.getParameterTypes();
        Object[] parameters = request.getParameters();

        //Using CGLIB Reflect
        FastClass fastClass = FastClass.create(serviceClass);
        FastMethod fastMethod = fastClass.getMethod(methodName, parameterTypes);
        log.info("Start calling CGLIB Dynamic Proxy Execution Server-side Method...");
        return fastMethod.invoke(serviceBean, parameters);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
