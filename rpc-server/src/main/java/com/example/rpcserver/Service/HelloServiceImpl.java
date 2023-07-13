package com.example.rpcserver.Service;

import com.example.rpcapi.api.HelloService;
import org.springframework.stereotype.Component;

/**
 * @author yangheng <yangheng@kuaishou.com>
 * Created on 2023-07-12
 */
@Component
public class HelloServiceImpl implements HelloService {
    @Override
    public String hello(String name) {
        return "hello " + name;
    }
}
