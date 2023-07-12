package com.example.rpcapi.Serializer.impl;

import java.io.IOException;

import com.alibaba.fastjson2.JSON;
import com.example.rpcapi.Serializer.Serializer;

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
}
