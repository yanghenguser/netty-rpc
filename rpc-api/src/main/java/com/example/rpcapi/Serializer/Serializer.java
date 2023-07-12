package com.example.rpcapi.Serializer;

import java.io.IOException;

/**
 * @author yangheng <yangheng@kuaishou.com>
 * Created on 2023-07-12
 */
public interface Serializer {
    /**
     * java Object to binary
     *
     * @param object
     * @return
     */
    byte[] serialize(Object object) throws IOException;

    /**
     * Binary conversion to java objects
     *
     * @param clazz
     * @param bytes
     * @param <T>
     * @return
     */
    <T> T deserialize(Class<T> clazz, byte[] bytes) throws IOException;
}
