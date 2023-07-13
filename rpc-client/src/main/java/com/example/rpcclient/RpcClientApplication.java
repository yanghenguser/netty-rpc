package com.example.rpcclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.example.rpcapi.api.HelloService;
import com.example.rpcclient.proxy.ProxyFactory;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class RpcClientApplication {

    public static void main(String[] args) throws Exception {
        HelloService helloService = ProxyFactory.create(HelloService.class);
        log.info("Response : {}", helloService.hello("1234"));

    }
}
