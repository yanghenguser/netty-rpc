package com.example.rpcserver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.example.rpcserver.server.NettyServer;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class RpcServerApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(RpcServerApplication.class, args);
        context.getBean(NettyServer.class).start();

    }

}
