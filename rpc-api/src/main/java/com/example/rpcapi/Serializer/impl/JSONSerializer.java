package com.example.rpcapi.Serializer.impl;

import java.io.IOException;

import com.alibaba.fastjson2.JSON;
import com.example.rpcapi.Serializer.Serializer;
import com.example.rpcapi.module.RpcRequest;

/**
 * @author yangheng <yangheng@kuaishou.com>
 * Created on 2023-07-12
 */
public class JSONSerializer implements Serializer {
    @Override
    public byte[] serialize(Object object) throws IOException {
        return JSON.toJSONBytes(object);
    }

    @Override
    public <T> T deserialize(Class<T> clazz, byte[] bytes) throws IOException {
        return JSON.parseObject(bytes, clazz);
    }

    public static void main(String[] args) throws IOException {
        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setRequestId("111");
        rpcRequest.setParameters(new Object[2]);
        rpcRequest.setParameterTypes(new Class[]{RpcRequest.class, RpcRequest.class});
        JSONSerializer jsonSerializer = new JSONSerializer();
        byte[] serialize = jsonSerializer.serialize(rpcRequest);
        RpcRequest deserialize = jsonSerializer.deserialize(RpcRequest.class, serialize);
        System.out.println(JSON.toJSONString(deserialize));

    }
}
